package com.campusconnect.backend.board.service;

import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.domain.BoardImage;
import com.campusconnect.backend.board.dto.request.BoardCreateRequest;
import com.campusconnect.backend.board.dto.request.BoardFavoriteRequest;
import com.campusconnect.backend.board.dto.request.BoardUpdateRequest;
import com.campusconnect.backend.board.dto.response.*;
import com.campusconnect.backend.board.repository.BoardImageRepository;
import com.campusconnect.backend.board.repository.BoardRepository;
import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.favorite.domain.Favorite;
import com.campusconnect.backend.favorite.service.FavoriteService;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final FavoriteService favoriteService;
    private final S3Uploader s3Uploader;

    /**
     * 판매 게시글 작성
     */
    @Transactional
    public BoardCreateResponse createBoard(BoardCreateRequest boardCreateRequest, List<MultipartFile> multipartFiles) throws IOException {
        User foundUser = userRepository.findByStudentNumber(boardCreateRequest.getStudentNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 이미지 업로드 및 URL 생성
        List<String> boardImageNames = new ArrayList<>();

        if (multipartFiles == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_BOARD_IMAGES);
        } else {
            for (MultipartFile file : multipartFiles) {
                String imageName = s3Uploader.upload(file, "board");
                boardImageNames.add(imageName);
            }
        }

        // 업로드한 게시글 이미지 수를 초과했다면 게시글을 등록할 수 없다.
        if (multipartFiles.size() > 10) {
            throw new CustomException(ErrorCode.EXCEEDED_LIMIT_BOARD_IMAGES);
        }

        Board board = Board.builder()
                .title(boardCreateRequest.getTitle())
                .content(boardCreateRequest.getContent())
                .user(foundUser)
                .build();
        Board savedBoard = boardRepository.save(board);

        List<BoardImage> boardImages = new ArrayList<>();
        for (String imageName : boardImageNames) {
            BoardImage image = BoardImage.builder()
                    .board(savedBoard)
                    .boardImage(imageName)
                    .build();
            boardImages.add(image);
        }
        boardImageRepository.saveAll(boardImages);

        return BoardCreateResponse.builder()
                .boardId(board.getId())
                .studentNumber(foundUser.getStudentNumber())
                .title(board.getTitle())
                .build();
    }

    /**
     * 특정 판매 게시글 조회 (게시글 상세 페이지)
     */
    public BoardDetailResponse getBoardById(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        return BoardDetailResponse.builder()
                .boardId(findBoard.getId())
                .image(findBoard.getUser().getImage())
                .department(findBoard.getUser().getDepartment())
                .name(findBoard.getUser().getName())
                .createDate(findBoard.getCreatedDate())
                .sellerManner(findBoard.getUser().getSellerManner())
                .boardImages(findBoard.getBoardImages())
                .title(findBoard.getTitle())
                .content(findBoard.getContent())
                .tradeStatus(findBoard.getTradeStatus())
                .favoriteCount(findBoard.getFavoriteCount())
                .chatCount(findBoard.getChatCount())
                .build();
    }

    /**
     * 게시글 전체(리스트) 조회
     */
    public List<BoardListResponse> getBoardList() {
        return boardRepository.findAll().stream()
                .map(board -> BoardListResponse.builder()
                        .boardId(board.getId())
                        .department(board.getUser().getDepartment())
                        .name(board.getUser().getName())
                        .userProfileImage(board.getUser().getImage())
                        .representativeImage(board.getBoardImages().get(0))
                        .title(board.getTitle())
                        .favoriteCount(board.getFavoriteCount())
                        .chatCount(board.getChatCount())
                        .tradeStatus(board.getTradeStatus())
                        .build())
                .collect(Collectors.toList());
    }

    /** 페이징 처리 : 전체 게시글 조회 */
    public List<BoardListResponse> getBoardListWithPaging(Pageable pageable) {
        return boardRepository.findAll(pageable).stream()
                .map(board -> BoardListResponse.builder()
                        .boardId(board.getId())
                        .department(board.getUser().getDepartment())
                        .name(board.getUser().getName())
                        .createDate(board.getCreatedDate())
                        .userProfileImage(board.getUser().getImage())
                        .representativeImage(board.getBoardImages().get(0))
                        .title(board.getTitle())
                        .favoriteCount(board.getFavoriteCount())
                        .chatCount(board.getChatCount())
                        .tradeStatus(board.getTradeStatus())
                        .build())
                .collect(Collectors.toList());
    }

    /** 학과, 게시글 제목으로 게시글 검색(조회) */
    public List<BoardListResponse> searchBoardWithSearchCond(String department, String title, Pageable pageable) {
        return boardRepository.searchBoardWithSearchCond(department, title, pageable).stream()
                .map(board -> BoardListResponse.builder()
                        .boardId(board.getId())
                        .department(board.getUser().getDepartment())
                        .name(board.getUser().getName())
                        .createDate(board.getCreatedDate())
                        .userProfileImage(board.getUser().getImage())
                        .representativeImage(board.getBoardImages().get(0))
                        .title(board.getTitle())
                        .favoriteCount(board.getFavoriteCount())
                        .chatCount(board.getChatCount())
                        .tradeStatus(board.getTradeStatus())
                        .build())
                .collect(Collectors.toList());
    }

    /** 게시글 수정 */
    @Transactional
    public void updateBoard(Long boardId,
                            BoardUpdateRequest boardUpdateRequest,
                            List<MultipartFile> multipartFiles) throws IOException {

        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        // 거래 완료인 경우 게시글 수정 불가
        findBoard.checkToBoardUpdateWithTradeStatus();

        findBoard.updateTitle(boardUpdateRequest.getTitle());
        findBoard.updateContent(boardUpdateRequest.getContent());

        // 새로운 사진 추가 및 기존 사진 삭제
//        updateImages(findBoard, boardUpdateRequest.getDeletedImages(), multipartFiles);

        // 수정 페이지에서 거래 완료로 변경하는 경우
        String tradeStatus = boardUpdateRequest.getTradeStatus();
        findBoard.changeTradeStatus(tradeStatus);
    }
//    private void updateImages(Board board,
//                              List<Long> deletedImageIds,
//                              List<MultipartFile> multipartFiles) throws IOException {
//        List<BoardImage> updatedImages = new ArrayList<>();
//
//        if (deletedImageIds != null) {
//            // 기존 이미지 삭제
//            for (BoardImage existingImage : board.getBoardImagesToBoardImageType()) {
//                if (!deletedImageIds.contains(existingImage.getId())) {
//                    updatedImages.add(existingImage);
//                } else {
//                    // S3에서 이미지 삭제
//                    s3Uploader.delete(existingImage.getBoardImage());
//                }
//            }
//        }
//
//        // 새로운 이미지 추가
//        int remainingImageSlots = 10 - updatedImages.size();
//
//        if (multipartFiles != null) {
//            for (MultipartFile file : multipartFiles) {
//                if (remainingImageSlots <= 0) {
//                    throw new CustomException(ErrorCode.EXCEEDED_LIMIT_BOARD_IMAGES);
//                }
//                String fileName = s3Uploader.upload(file, "static");
//                updatedImages.add(new BoardImage(board, fileName));
//                remainingImageSlots--;
//            }
//        }
//
//

//        // 기존 이미지 업데이트
//        board.updateBoardImages(updatedImages);

//    }

    /** 관심 상품으로 등록 */
    @Transactional
    public BoardFavoriteResponse registerToFavoriteBoard(Long boardId, BoardFavoriteRequest boardFavoriteRequest) {
        Favorite savedFavorite = favoriteService.saveFavorite(boardId, boardFavoriteRequest);

        return BoardFavoriteResponse.builder()
                .boardId(boardId)
                .favoriteCount(savedFavorite.getBoard().getFavoriteCount())
                .userName(savedFavorite.getUser().getName())
                .studentNumber(savedFavorite.getUser().getStudentNumber())
                .errorCode(ErrorCode.SUCCESS_REGISTER_FAVORITE_BOARD)
                .build();
    }

    /** 관심 상품으로 등록 취소 */
    @Transactional
    public BoardFavoriteResponse cancelToFavoriteBoard(Long boardId, BoardFavoriteRequest boardFavoriteRequest) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        User findUser = userRepository.findByStudentNumber(boardFavoriteRequest.getStudentNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        favoriteService.cancelFavorite(boardId, boardFavoriteRequest);

        return BoardFavoriteResponse.builder()
                .boardId(boardId)
                .favoriteCount(findBoard.getFavoriteCount())
                .userName(findUser.getName())
                .studentNumber(findUser.getStudentNumber())
                .errorCode(ErrorCode.SUCCESS_CANCEL_FAVORITE_BOARD)
                .build();
    }

    /** 게시글 삭제 */
    @Transactional
    public BoardDeleteResponse deleteBoard(Long boardId){
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        // 거래 완료인 경우 게시글 삭제 불가
        findBoard.checkToBoardDeleteWithTradeStatus();

        deleteFromS3Bucket(findBoard);
        boardRepository.delete(findBoard);

        // 게시글 삭제 시 해당 게시글과 관련된 관심 게시글 내역도 모두 삭제
        favoriteService.deleteAllFavorites(boardId);

        return BoardDeleteResponse.builder()
                .boardId(findBoard.getId())
                .title(findBoard.getTitle())
                .errorCode(ErrorCode.SUCCESS_BOARD_DELETE.getDescription())
                .build();
    }

    public void deleteFromS3Bucket(Board findBoard) {
        List<String> findBoardImages = findBoard.getBoardImagesToBoardImageType().stream()
                .map(BoardImage::getBoardImage)
                .collect(Collectors.toList());

        for (String image : findBoardImages) {
            String bucketUrl = "https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/board/";
            String imageName = image.replace(bucketUrl, "");
            log.info("imageName = {}", imageName);
            s3Uploader.deleteToBoardImage(imageName);
        }
    }

    public void deleteFromS3BucketToMultipleBoards(List<Board> findBoards) {
        for (Board findBoard : findBoards) {
            List<String> findBoardImages = findBoard.getBoardImagesToBoardImageType().stream()
                    .map(BoardImage::getBoardImage)
                    .collect(Collectors.toList());

            for (String image : findBoardImages) {
                String bucketUrl = "https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/board/";
                String imageName = image.replace(bucketUrl, "");
                log.info("imageName = {}", imageName);
                s3Uploader.deleteToBoardImage(imageName);
            }
        }
    }
}
