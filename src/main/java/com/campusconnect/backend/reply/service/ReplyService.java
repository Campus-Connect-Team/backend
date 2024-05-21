package com.campusconnect.backend.reply.service;

import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.repository.BoardRepository;
import com.campusconnect.backend.comment.domain.Comment;
import com.campusconnect.backend.comment.repository.CommentRepository;
import com.campusconnect.backend.reply.domain.Reply;
import com.campusconnect.backend.reply.dto.request.ReplyCreateRequest;
import com.campusconnect.backend.reply.dto.request.ReplyUpdateRequest;
import com.campusconnect.backend.reply.dto.response.*;
import com.campusconnect.backend.reply.repository.ReplyRepository;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReplyService {

    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    /** 특정 댓글에 대한 답글 작성 */
    @Transactional
    public ReplyCreateResponse createReply(String studentNumber,
                                           Long boardId,
                                           Long commentId,
                                           ReplyCreateRequest replyCreateRequest) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_COMMENTS));

        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Reply createdReply = Reply.builder()
                .comment(findComment)
                .user(findUser)
                .replierName(findUser.getName())
                .replierStudentNumber(findUser.getStudentNumber())
                .replierDepartment(findUser.getDepartment())
                .replyContent(replyCreateRequest.getReplyContent())
                .build();
        replyRepository.save(createdReply);

        return ReplyCreateResponse.builder()
                .boardId(findBoard.getId())
                .commentId(findComment.getId())
                .replyId(createdReply.getId())
                .replierName(createdReply.getReplierName())
                .replierDepartment(createdReply.getReplierDepartment())
                .replyContent(createdReply.getReplyContent())
                .createdAt(createdReply.getCreatedDate())
                .responseCode(ErrorCode.SUCCESS_REPLY_CREATION.getDescription())
                .build();
    }

    /** 특정 댓글에 대한 답글 리스트 조회 */
    public ReplyAllListResponse getAllReplies(Long boardId, Long commentId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_COMMENTS));
        List<Reply> findReplies = replyRepository.findAllReplyByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_REPLIES));

        List<ReplySingleResponse> replySingleResponses = findReplies.stream()
                .map(findReply -> ReplySingleResponse.builder()
                        .replyId(findReply.getId())
                        .replierProfileImage(findReply.getUser().getImage())
                        .replierDepartment(findReply.getReplierDepartment())
                        .replierName(findReply.getReplierName())
                        .replyContent(findReply.getReplyContent())
                        .modifiedAt(findReply.getModifiedDate())
                        .build())
                .collect(Collectors.toList());

        return ReplyAllListResponse.builder()
                .boardId(findBoard.getId())
                .commentId(findComment.getId())
                .replySingleResponses(replySingleResponses)
                .build();
    }

    /** 특정 댓글에 대한 답글 수정 */
    @Transactional
    public ReplyUpdateResponse updateReply(String studentNumber,
                                           Long boardId,
                                           Long commentId,
                                           Long replyId,
                                           ReplyUpdateRequest replyUpdateRequest) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_COMMENTS));
        Reply findReply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_REPLIES));

        // 본인이 작성한 답글이 맞는지 검증
        if (!replyRepository.isReplyWrittenByRelevantUser(commentId, studentNumber)) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_OR_DELETE_REPLY_BECAUSE_NOT_RELEVANT_USER);
        }

        findReply.updateReply(replyUpdateRequest.getReplyUpdateContent());

        return ReplyUpdateResponse.builder()
                .replyId(findReply.getId())
                .replierUserProfile(findReply.getUser().getImage())
                .replierDepartment(findReply.getReplierDepartment())
                .replierName(findReply.getReplierName())
                .replyContent(findReply.getReplyContent())
                .modifiedAt(findReply.getModifiedDate())
                .responseCode(ErrorCode.SUCCESS_REPLY_UPDATE.getDescription())
                .build();
    }

    /** 특정 댓글에 대한 답글 삭제 */
    @Transactional
    public ReplyDeleteResponse deleteReply(String studentNumber,
                                           Long boardId,
                                           Long commentId,
                                           Long replyId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_COMMENTS));
        Reply findReply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_REPLIES));

        // 본인이 작성한 답글이 맞는지 검증
        if (!replyRepository.isReplyWrittenByRelevantUser(commentId, studentNumber)) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_OR_DELETE_REPLY_BECAUSE_NOT_RELEVANT_USER);
        }
        replyRepository.delete(findReply);

        return ReplyDeleteResponse.builder()
                .replyId(findReply.getId())
                .responseCode(ErrorCode.SUCCESS_REPLY_DELETE.getDescription())
                .build();
    }
}
