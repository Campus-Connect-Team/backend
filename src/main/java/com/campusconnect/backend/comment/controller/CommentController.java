package com.campusconnect.backend.comment.controller;

import com.campusconnect.backend.comment.dto.request.CommentCreateRequest;
import com.campusconnect.backend.comment.dto.request.CommentUpdateRequest;
import com.campusconnect.backend.comment.dto.response.*;
import com.campusconnect.backend.comment.service.CommentService;
import com.campusconnect.backend.util.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final JwtProvider jwtProvider;

    @Value("${jwt.secret-key}")
    private String secretKey;

    /** 댓글 작성 */
    @Operation(summary = "특정 게시글에 댓글 작성", description = "특정 게시글에 대해 댓글을 작성할 수 있다.")
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentCreateResponse> createComment(HttpServletRequest request,
                                                               @PathVariable Long boardId,
                                                               @RequestBody @Valid CommentCreateRequest commentCreateRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.createComment(boardId, studentNumber, commentCreateRequest));
    }

    /** 전체 댓글 조회 */
    @Operation(summary = "특정 게시글에 작성된 전체 댓글 조회", description = "특정 게시글에 대해 작성되어 있는 모든 댓글을 조회한다. ")
    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<BoardCommentAllListResponse> getAllBoardComments(@PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getAllBoardComments(boardId));
    }

    /** 특정 댓글 조회 */
    @Operation(summary = "게시글에 작성된 특정 댓글 조회", description = "게시글에 대해 작성된 특정 댓글을 조회한다. ")
    @GetMapping("/boards/{boardId}/comments/{commentId}")
    public ResponseEntity<BoardCommentResponse> getBoardComment(@PathVariable Long boardId, @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getBoardComment(boardId, commentId));
    }

    /** 댓글 수정 */
    @Operation(summary = "특정 게시글에 작성된 댓글을 수정", description = "특정 게시글에 대해 작성되어 있는 기존 댓글을 수정한다. ")
    @PatchMapping("/boards/{boardId}/comments/{commentId}")
    public ResponseEntity<CommentUpdateResponse> updateBoardComment(HttpServletRequest request,
                                                                    @PathVariable Long boardId,
                                                                    @PathVariable Long commentId,
                                                                    @RequestBody @Valid CommentUpdateRequest commentUpdateRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.updateBoardComment(studentNumber, boardId, commentId, commentUpdateRequest));
    }

    /** 댓글 삭제 */
    @Operation(summary = "특정 게시글에 작성된 댓글을 삭제", description = "특정 게시글에 대해 작성되어 있는 기존 댓글을 삭제한다. ")
    @DeleteMapping("/boards/{boardId}/comments/{commentId}")
    public ResponseEntity<CommentDeleteResponse> deleteBoardComment(HttpServletRequest request,
                                                                    @PathVariable Long boardId,
                                                                    @PathVariable Long commentId) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.deleteBoardComment(studentNumber, boardId, commentId));
    }
}
