package com.campusconnect.backend.board.controller;

import com.campusconnect.backend.board.dto.request.BoardCreateRequest;
import com.campusconnect.backend.board.dto.request.BoardUpdateRequest;
import com.campusconnect.backend.board.dto.response.*;
import com.campusconnect.backend.board.service.BoardService;
import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final JwtProvider jwtProvider;
    private final BoardService boardService;
    private final S3Uploader s3Uploader;

    @Value("${jwt.secret-key}")
    private String secretKey;

    /** 판매 게시글 작성 */
    @Operation(summary = "본인이 판매하고 싶은 상품에 대한 판매 게시글을 작성", description = "거래 게시판에 본인의 판매 게시글을 등록할 수 있다.")
    @PostMapping(value = "/boards", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BoardCreateResponse> createBoard(HttpServletRequest request,
                                                           @RequestPart(value = "request") @Valid BoardCreateRequest boardCreateRequest,
                                                           @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles) throws IOException {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK.value())
                .body(boardService.createBoard(boardCreateRequest, studentNumber, multipartFiles));
    }

    /** 특정 게시글 조회 (게시글 상세 페이지) */
    @Operation(summary = "거래 게시판에서 특정 게시글을 클릭 시 조회되는 게시글 상세 페이지", description = "특정 게시글의 상세 페이지에서 판매자 정보, 상품에 대한 내용, " +
            "댓글 리스트 등 전반적인 리스트를 확인할 수 있다.")
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardDetailResponse> getBoardById(@PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK.value())
                .body(boardService.getBoardById(boardId));
    }

    /** 게시글 전체(리스트) 조회 */
    @GetMapping("/boards")
    public ResponseEntity<List<BoardListResponse>> getBoardList() {
        return ResponseEntity.status(HttpStatus.OK.value())
                .body(boardService.getBoardList());
    }

    /** 페이징 처리 : 게시글 조회 */
//    @Operation(summary = "페이징 처리", description = "거래 게시판에서 각 페이지 당 9개의 게시글이 노출된다. 각 게시글의 등록시간 순으로 내림차순으로 정렬되어 조회된다.")
//    @GetMapping("/boards")
//    public ResponseEntity<List<BoardListResponse>> getBoardListWithPaging(@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(boardService.getBoardListWithPaging(pageable));
//    }

    /** 학과, 게시글 제목으로 게시글 검색(조회) */
    @Operation(summary = "거래 게시판에서 검색 필터링(학과, 제목) 기능으로 게시글 조회", description = "거래 게시판에서 학과 또는 게시글 제목 / 아니면 학과, 게시글을 모두 입력해 특정한" +
            "게시글이 존재하는지 검색해볼 수 있다.")
    @GetMapping("/boards/search")
    public ResponseEntity<List<BoardListResponse>> searchBoardWithSearchCond(@RequestParam(required = false) String department,
                                                                             @RequestParam(required = false) String title,
                                                                             @PageableDefault(size = 9, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        if ((boardService.searchBoardWithSearchCond(department, title, pageable).isEmpty())) {
            throw new CustomException(ErrorCode.NOT_EXISTS_SEARCH_BOARD_RESULTS);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.searchBoardWithSearchCond(department, title, pageable));
    }

    /** 게시글 수정 */
    @Operation(summary = "거래 게시글 수정", description = "본인의 마이 페이지에서 작성한 판매 게시글의 내용을 수정할 수 있다.(게시글 제목, 내용, 업로드 사진, 거래 상태")
    @PatchMapping(value = "/boards/{boardId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BoardUpdateResponse> updateBoard(HttpServletRequest request,
                                                           @PathVariable Long boardId,
                                                           @RequestPart(value = "request") BoardUpdateRequest boardUpdateRequest,
                                                           @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles) throws IOException {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK.value())
                .body(boardService.updateBoard(boardId, boardUpdateRequest, studentNumber, multipartFiles));
    }

    /** 관심 상품(게시글)으로 등록 */
    @Operation(summary = "관심 게시글(상품)으로 등록", description = "거래 게시글 상세 페이지에서 본인이 마음에 드는 게시글을 관심 상품으로 등록할 수 있다.")
    @PatchMapping("/boards/{boardId}/favorite/register")
    public ResponseEntity<BoardFavoriteResponse> registerToFavoriteBoard(HttpServletRequest request, @PathVariable Long boardId) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.registerToFavoriteBoard(boardId, studentNumber));
    }

    /** 관심 상품(게시글) 등록 취소 */
    @Operation(summary = "관심 게시글(상품)으로 등록을 취소", description = "거래 게시글 상세 페이지 또는 본인의 마이 페이지 -> 관심 상품 리스트에서 관심 게시글(상품)에서 제외시킬 수 있다.")
    @PatchMapping("/boards/{boardId}/favorite/cancel")
    public ResponseEntity<BoardFavoriteResponse> cancelToFavoriteBoard(HttpServletRequest request, @PathVariable Long boardId) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.cancelToFavoriteBoard(boardId, studentNumber));
    }

    /** 게시글 삭제 */
    @Operation(summary = "거래 게시글 삭제", description = "본인이 작성한 거래 게시글을 삭제할 수 있다. 단, 거래 완료 상태에서는 삭제가 불가능하다.")
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<BoardDeleteResponse> deleteBoard(HttpServletRequest request, @PathVariable Long boardId) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK.value())
                .body(boardService.deleteBoard(studentNumber, boardId));
    }
}
