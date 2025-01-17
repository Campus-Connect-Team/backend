package com.campusconnect.backend.board.service;

import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.domain.BoardImage;
import com.campusconnect.backend.board.dto.request.BoardCreateRequest;
import com.campusconnect.backend.board.dto.request.BoardUpdateRequest;
import com.campusconnect.backend.board.dto.response.*;
import com.campusconnect.backend.board.repository.BoardImageRepository;
import com.campusconnect.backend.board.repository.BoardRepository;
import com.campusconnect.backend.comment.domain.Comment;
import com.campusconnect.backend.comment.repository.CommentRepository;
import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.favorite.domain.Favorite;
import com.campusconnect.backend.favorite.service.FavoriteService;
import com.campusconnect.backend.reply.domain.Reply;
import com.campusconnect.backend.reply.repository.ReplyRepository;
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
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final FavoriteService favoriteService;
    private final S3Uploader s3Uploader;

    /** 판매 게시글 작성 */
    @Transactional
    public BoardCreateResponse createBoard(BoardCreateRequest boardCreateRequest,
                                           String studentNumber,
                                           List<MultipartFile> multipartFiles) throws IOException {
        User foundUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 이미지 업로드 및 URL 생성
        List<String> boardImageNames = new ArrayList<>();

        if (multipartFiles == null || multipartFiles.isEmpty() || multipartFiles.stream().allMatch(MultipartFile::isEmpty)) {
            throw new CustomException(ErrorCode.NOT_FOUND_BOARD_IMAGES);
        }

        // 업로드한 게시글 이미지 수를 초과했다면 게시글을 등록할 수 없다.
        if (multipartFiles.size() > 10) {
            throw new CustomException(ErrorCode.EXCEEDED_LIMIT_BOARD_IMAGES);
        } else {
            for (MultipartFile file : multipartFiles) {
                String imageName = s3Uploader.upload(file, "board");
                boardImageNames.add(imageName);
            }
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
                .responseCode(ErrorCode.SUCCESS_BOARD_CREATION.getDescription())
                .build();
    }

    /** 특정 판매 게시글 조회 (게시글 상세 페이지) */
    public BoardDetailResponse getBoardById(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        List<Comment> comments = commentRepository.findAllCommentByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        List<Reply> replies = replyRepository.findAllRepliesByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_REPLIES));

        // 댓글 리스트
        List<BoardCommentDetailResponse> boardCommentDetailResponses = comments.stream()
                .map(comment -> {
                    // 해당 댓글에 대한 답글 리스트 필터링
                    List<BoardReplyDetailResponse> filteredReplies = replies.stream()
                            .filter(reply -> reply.getComment().getId().equals(comment.getId()))
                            .map(reply -> BoardReplyDetailResponse.builder()
                                    .replyId(reply.getId())
                                    .replierProfileImage(reply.getUser().getImage())
                                    .replierDepartment(reply.getReplierDepartment())
                                    .replierName(reply.getReplierName())
                                    .replyContent(reply.getReplyContent())
                                    .modifiedAt(reply.getModifiedDate())
                                    .build())
                            .collect(Collectors.toList());

                    return BoardCommentDetailResponse.builder()
                            .commentId(comment.getId())
                            .commenterProfileImage(comment.getUser().getImage())
                            .commenterDepartment(comment.getCommenterDepartment())
                            .commenterName(comment.getCommenterName())
                            .commentContent(comment.getCommentContent())
                            .modifiedAt(comment.getModifiedDate())
                            .boardReplyDetailResponses(filteredReplies)
                            .build();
                })
                .collect(Collectors.toList());

        return BoardDetailResponse.builder()
                .boardId(findBoard.getId())
                .image(findBoard.getUser().getImage())
                .department(findBoard.getUser().getDepartment())
                .name(findBoard.getUser().getName())
                .createdAt(findBoard.getCreatedDate())
                .boardImages(findBoard.getBoardImages())
                .title(findBoard.getTitle())
                .content(findBoard.getContent())
                .tradeStatus(findBoard.getTradeStatus())
                .favoriteCount(findBoard.getFavoriteCount())
                .commentCount(findBoard.getCommentCount())
                .boardCommentDetailResponses(boardCommentDetailResponses)
                .build();
    }

    /** 게시글 전체(리스트) 조회 */
    public List<BoardListResponse> getBoardList() {
        int size = boardRepository.findAll().size();
        System.out.println("size" + size);
        System.out.println("size" + size);
        log.info("total size: {}", String.valueOf(size));
        return boardRepository.findAll().stream()
                .map(board -> BoardListResponse.builder()
                        .boardId(board.getId())
                        .department(board.getUser().getDepartment())
                        .name(board.getUser().getName())
                        .userProfileImage(board.getUser().getImage())
                        .representativeImage(board.getBoardImages().get(0))
                        .title(board.getTitle())
                        .favoriteCount(board.getFavoriteCount())
                        .commentCount(board.getCommentCount())
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
                        .commentCount(board.getCommentCount())
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
                        .commentCount(board.getCommentCount())
                        .tradeStatus(board.getTradeStatus())
                        .build())
                .collect(Collectors.toList());
    }

    /** 게시글 수정 */
    @Transactional
    public BoardUpdateResponse updateBoard(Long boardId,
                            BoardUpdateRequest boardUpdateRequest,
                            String studentNumber,
                            List<MultipartFile> multipartFiles) throws IOException {

        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        // 본인이 작성한 글이 아니라면 수정 불가
        if (!findBoard.getUser().getStudentNumber().equals(studentNumber)) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_OR_DELETE_BOARD_BECAUSE_NOT_RELEVANT_USER);
        }

        // 거래 완료인 경우 게시글 수정 불가
        findBoard.checkToBoardUpdateWithTradeStatus();

        findBoard.updateTitle(boardUpdateRequest.getTitle());
        findBoard.updateContent(boardUpdateRequest.getContent());

        // 수정 페이지에서 거래 완료로 변경하는 경우
        String tradeStatus = boardUpdateRequest.getTradeStatus();
        findBoard.changeTradeStatus(tradeStatus);

        // 게시글 업로드 사진 변경
        updateBoardImages(multipartFiles, findBoard);

        return BoardUpdateResponse.builder()
                .name(findBoard.getUser().getName())
                .department(findBoard.getUser().getDepartment())
                .boardId(findBoard.getId())
                .title(findBoard.getTitle())
                .content(findBoard.getContent())
                .tradeStatus(findBoard.getTradeStatus())
                .responseCode(ErrorCode.SUCCESS_BOARD_UPDATE.getDescription())
                .build();
    }

    private void updateBoardImages(List<MultipartFile> multipartFiles, Board findBoard) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty() || multipartFiles.stream().allMatch(MultipartFile::isEmpty)) {
            throw new CustomException(ErrorCode.NOT_FOUND_BOARD_IMAGES);
        }

        if (multipartFiles.size() > 10) {
            throw new CustomException(ErrorCode.EXCEEDED_LIMIT_BOARD_IMAGES);
        }

        // 기존 이미지를 DB에서 삭제 후 S3 Bucket에서도 삭제
        List<String> boardImages = findBoard.getBoardImages();
        for (String boardImage : boardImages) {
            String realBoardImage = boardImage.replace("https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/board/", "");
            s3Uploader.deleteToBoardImage(realBoardImage);
        }
        boardImageRepository.deleteAllByBoardId(findBoard.getId());

        List<BoardImage> boardImageNames = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            String updateBoardImage = s3Uploader.upload(file, "board");
            BoardImage boardImage = new BoardImage();
            boardImage.updateImageUrl(updateBoardImage);
            boardImage.assignBoardId(findBoard);
            boardImageNames.add(boardImage);
        }
        boardImageRepository.saveAll(boardImageNames);
    }

    /** 관심 상품으로 등록 */
    @Transactional
    public BoardFavoriteResponse registerToFavoriteBoard(Long boardId, String studentNumber) {
        Favorite savedFavorite = favoriteService.saveFavorite(boardId, studentNumber);

        return BoardFavoriteResponse.builder()
                .boardId(boardId)
                .favoriteCount(savedFavorite.getBoard().getFavoriteCount())
                .userName(savedFavorite.getUser().getName())
                .studentNumber(savedFavorite.getUser().getStudentNumber())
                .responseCode(ErrorCode.SUCCESS_REGISTER_FAVORITE_BOARD.getDescription())
                .build();
    }

    /** 관심 상품으로 등록 취소 */
    @Transactional
    public BoardFavoriteResponse cancelToFavoriteBoard(Long boardId, String studentNumber) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        favoriteService.cancelFavorite(boardId, studentNumber);

        return BoardFavoriteResponse.builder()
                .boardId(boardId)
                .favoriteCount(findBoard.getFavoriteCount())
                .userName(findUser.getName())
                .studentNumber(findUser.getStudentNumber())
                .responseCode(ErrorCode.SUCCESS_CANCEL_FAVORITE_BOARD.getDescription())
                .build();
    }

    /** 게시글 삭제 */
    @Transactional
    public BoardDeleteResponse deleteBoard(String studentNumber, Long boardId){
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        // 본인이 작성한 글이 아니라면 삭제 불가
        if (!findBoard.getUser().getStudentNumber().equals(studentNumber)) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_OR_DELETE_BOARD_BECAUSE_NOT_RELEVANT_USER);
        }

        // 거래 완료인 경우 게시글 삭제 불가
        findBoard.checkToBoardDeleteWithTradeStatus();

        deleteFromS3Bucket(findBoard);
        boardImageRepository.deleteAllByBoardId(findBoard.getId());
        boardRepository.delete(findBoard);

        // 게시글 삭제 시 해당 게시글과 관련된 관심 게시글 내역도 모두 삭제
        favoriteService.deleteAllFavorites(boardId);

        // 게시글 삭제 시 해당 게시글과 관련된 댓글, 답글 내역도 모두 삭제
        replyRepository.deleteAllByBoardId(boardId);
        commentRepository.deleteByAllBoardId(boardId);

        return BoardDeleteResponse.builder()
                .boardId(findBoard.getId())
                .title(findBoard.getTitle())
                .responseCode(ErrorCode.SUCCESS_BOARD_DELETE.getDescription())
                .build();
    }

    /** 회원 탈퇴 시 모든 게시글 삭제 */
    public void deleteAllBoard(Long id) {
        boardRepository.deleteByUserId(id);
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
