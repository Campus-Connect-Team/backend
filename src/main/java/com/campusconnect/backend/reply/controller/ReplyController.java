package com.campusconnect.backend.reply.controller;

import com.campusconnect.backend.reply.dto.request.ReplyCreateRequest;
import com.campusconnect.backend.reply.dto.request.ReplyUpdateRequest;
import com.campusconnect.backend.reply.dto.response.ReplyAllListResponse;
import com.campusconnect.backend.reply.dto.response.ReplyCreateResponse;
import com.campusconnect.backend.reply.dto.response.ReplyDeleteResponse;
import com.campusconnect.backend.reply.dto.response.ReplyUpdateResponse;
import com.campusconnect.backend.reply.service.ReplyService;
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
public class ReplyController {

    private final ReplyService replyService;
    private final JwtProvider jwtProvider;

    @Value("${jwt.secret-key}")
    private String secretKey;

    /** 특정 댓글에 대한 답글 작성 */
    @Operation(summary = "특정 댓글에 대한 답글을 작성", description = "게시글 상세 페이지에서 특정 댓글에 대해 사용자가 답글을 작성할 수 있다.")
    @PostMapping("/boards/{boardId}/comments/{commentId}/replies")
    public ResponseEntity<ReplyCreateResponse> createReply(HttpServletRequest request,
                                                           @PathVariable Long boardId,
                                                           @PathVariable Long commentId,
                                                           @RequestBody @Valid ReplyCreateRequest replyCreateRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK)
                .body(replyService.createReply(studentNumber, boardId, commentId, replyCreateRequest));
    }

    /** 특정 댓글에 대한 답글 리스트 조회 */
    @Operation(summary = "특정 댓글에 대한 답글 리스트를 모두 조회", description = "게시글 상세 페이지에서 특정 댓글에 대해 사용자들이 작성한 답글을 조회할 수 있다.")
    @GetMapping("/boards/{boardId}/comments/{commentId}/replies")
    public ResponseEntity<ReplyAllListResponse> getAllReplies(@PathVariable Long boardId, @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(replyService.getAllReplies(boardId, commentId));
    }

    /** 특정 댓글에 대한 답글 수정 */
    @Operation(summary = "특정 댓글에 대한 답글을 수정", description = "게시글 상세 페이지에서 특정 댓글에 대해 사용자가 답글을 수정할 수 있다.")
    @PatchMapping("/boards/{boardId}/comments/{commentId}/replies/{replyId}")
    public ResponseEntity<ReplyUpdateResponse> updateReply(HttpServletRequest request,
                                                           @PathVariable Long boardId,
                                                           @PathVariable Long commentId,
                                                           @PathVariable Long replyId,
                                                           @RequestBody @Valid ReplyUpdateRequest replyUpdateRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK)
                .body(replyService.updateReply(studentNumber, boardId, commentId, replyId, replyUpdateRequest));
    }

    /** 특정 댓글에 대한 답글 삭제 */
    @Operation(summary = "특정 댓글에 대한 답글을 삭제", description = "게시글 상세 페이지에서 특정 댓글에 대해 사용자가 답글을 삭제할 수 있다.")
    @DeleteMapping("/boards/{boardId}/comments/{commentId}/replies/{replyId}")
    public ResponseEntity<ReplyDeleteResponse> deleteReply(HttpServletRequest request,
                                                           @PathVariable Long boardId,
                                                           @PathVariable Long commentId,
                                                           @PathVariable Long replyId) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);

        return ResponseEntity.status(HttpStatus.OK)
                .body(replyService.deleteReply(studentNumber, boardId, commentId, replyId));
    }
}
