package com.campusconnect.backend.board.controller;

import com.campusconnect.backend.board.dto.request.BoardCreateRequest;
import com.campusconnect.backend.board.dto.request.BoardUpdateRequest;
import com.campusconnect.backend.board.dto.response.BoardCreateResponse;
import com.campusconnect.backend.board.dto.response.BoardDetailResponse;
import com.campusconnect.backend.board.dto.response.BoardListResponse;
import com.campusconnect.backend.board.dto.response.BoardUpdateResponse;
import com.campusconnect.backend.board.service.BoardService;
import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.util.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    private final BoardService boardService;
    private final S3Uploader s3Uploader;

    /** 판매 게시글 작성 */
    @PostMapping(value = "/boards", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BoardCreateResponse> createBoard(HttpServletRequest request,
                                                           @RequestPart(value = "request") @Valid BoardCreateRequest boardCreateRequest,
                                                           @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles) throws IOException {
        return ResponseEntity.status(HttpStatus.OK.value())
                .body(boardService.createBoard(boardCreateRequest, multipartFiles));
    }

    /** 특정 게시글 조회 (게시글 상세 페이지) */
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardDetailResponse> getBoardById(@PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK.value())
                .body(boardService.getBoardById(boardId));
    }

//    /** 게시글 전체(리스트) 조회 */
//    @GetMapping("/boards")
//    public ResponseEntity<List<BoardListResponse>> getBoardList() {
//        return ResponseEntity.status(HttpStatus.OK.value())
//                .body(boardService.getBoardList());
//    }

    /** 페이징 처리 : 게시글 조회 */
    @GetMapping("/boards")
    public ResponseEntity<List<BoardListResponse>> getBoardListWithPaging(@PageableDefault(size = 9, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.getBoardListWithPaging(pageable));
    }

    /** 학과, 게시글 제목으로 게시글 검색(조회) */
    @GetMapping("/boards/search")
    public ResponseEntity<List<BoardListResponse>> searchBoardWithSearchCond(@RequestParam(required = false) String department,
                                                                             @RequestParam(required = false) String title,
                                                                             @PageableDefault(size = 9, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.searchBoardWithSearchCond(department, title, pageable));
    }


    /** 게시글 수정 */
    @PatchMapping(value = "/boards/{boardId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BoardUpdateResponse> updateBoard(@PathVariable Long boardId,
                                                           @RequestPart(value = "request") BoardUpdateRequest boardUpdateRequest,
                                                           @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles) throws IOException {

        boardService.updateBoard(boardId, boardUpdateRequest, multipartFiles);
        return ResponseEntity.status(HttpStatus.OK.value())
                .body(new BoardUpdateResponse(boardId, ErrorCode.SUCCESS_BOARD_UPDATE));
    }

    /** 게시글 삭제 */
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<ErrorCode> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.status(HttpStatus.OK.value())
                .body(ErrorCode.SUCCESS_BOARD_DELETE);
    }
}
