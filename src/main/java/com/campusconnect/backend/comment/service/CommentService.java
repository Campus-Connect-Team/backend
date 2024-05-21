package com.campusconnect.backend.comment.service;

import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.repository.BoardRepository;
import com.campusconnect.backend.comment.domain.Comment;
import com.campusconnect.backend.comment.dto.request.CommentCreateRequest;
import com.campusconnect.backend.comment.dto.request.CommentUpdateRequest;
import com.campusconnect.backend.comment.dto.response.*;
import com.campusconnect.backend.comment.repository.CommentRepository;
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
public class CommentService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    /** 댓글 작성 */
    @Transactional
    public CommentCreateResponse createComment(Long boardId,
                                               String studentNumber,
                                               CommentCreateRequest commentCreateRequest) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Comment createdComment = Comment.builder()
                .board(findBoard)
                .user(findUser)
                .commenterName(findUser.getName())
                .commenterDepartment(findUser.getDepartment())
                .commentContent(commentCreateRequest.getCommentContent())
                .build();
        commentRepository.save(createdComment);

        // 해당 게시글의 댓글 수 증가
        findBoard.increaseCommentCount();

        return CommentCreateResponse.builder()
                .boardId(findBoard.getId())
                .commentCount(findBoard.getCommentCount())
                .commentId(createdComment.getId())
                .commenterName(findUser.getName())
                .commenterDepartment(findUser.getDepartment())
                .commentContent(createdComment.getCommentContent())
                .createdAt(createdComment.getCreatedDate())
                .responseCode(ErrorCode.SUCCESS_COMMENT_CREATION.getDescription())
                .build();
    }

    /** 댓글 조회 */
    public BoardCommentAllListResponse getAllBoardComments(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        List<Comment> findComments = commentRepository.findAllCommentByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_COMMENTS));

        List<BoardCommentResponse> boardCommentResponses = findComments.stream()
                .map(findComment -> BoardCommentResponse.builder()
                        .commentId(findComment.getId())
                        .commenterProfileImage(findComment.getUser().getImage())
                        .commenterDepartment(findComment.getCommenterDepartment())
                        .commenterName(findComment.getCommenterName())
                        .commentContent(findComment.getCommentContent())
                        .modifiedAt(findComment.getModifiedDate())
                        .build())
                .collect(Collectors.toList());

        // BoardCommentResponse를 통해 게시글에 대한 댓글 내역 리스트 모두 조회
        return BoardCommentAllListResponse.builder()
                .boardId(findBoard.getId())
                .commentCount(findBoard.getCommentCount())
                .boardCommentResponses(boardCommentResponses)
                .build();
    }

    /** 특정 댓글 조회 */
    public BoardCommentResponse getBoardComment(Long boardId, Long commentId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        Comment findComment = commentRepository.findSingleCommentByBoardId(boardId, commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_COMMENTS));

        return BoardCommentResponse.builder()
                .commentId(findComment.getId())
                .commenterProfileImage(findComment.getUser().getImage())
                .commenterDepartment(findComment.getCommenterDepartment())
                .commenterName(findComment.getCommenterName())
                .commentContent(findComment.getCommentContent())
                .modifiedAt(findComment.getModifiedDate())
                .build();
    }

    /** 댓글 수정 */
    @Transactional
    public CommentUpdateResponse updateBoardComment(String studentNumber,
                                                    Long boardId,
                                                    Long commentId,
                                                    CommentUpdateRequest commentUpdateRequest) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_COMMENTS));

        // 본인이 작성한 댓글이 맞는지 검증
        if (!commentRepository.isCommentWrittenByRelevantUser(boardId, studentNumber)) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_OR_DELETE_COMMENT_BECAUSE_NOT_RELEVANT_USER);
        }

        findComment.updateComment(commentUpdateRequest.getCommentUpdateContent());

        return CommentUpdateResponse.builder()
                .commentId(findComment.getId())
                .commenterName(findComment.getCommenterName())
                .commenterDepartment(findComment.getCommenterDepartment())
                .commentContent(findComment.getCommentContent())
                .modifiedAt(findComment.getModifiedDate())
                .responseCode(ErrorCode.SUCCESS_COMMENT_UPDATE.getDescription())
                .build();
    }

    /** 댓글 삭제 */
    @Transactional
    public CommentDeleteResponse deleteBoardComment(String studentNumber,
                                                    Long boardId,
                                                    Long commentId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_COMMENTS));

        // 본인이 작성한 댓글이 맞는지 검증
        if (!commentRepository.isCommentWrittenByRelevantUser(boardId, studentNumber)) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_OR_DELETE_COMMENT_BECAUSE_NOT_RELEVANT_USER);
        }

        // 댓글 삭제 시 댓글에 달려있던 답글도 모두 삭제
        replyRepository.deleteAllByCommentId(commentId);
//        findBoard.deleteCommentAndDecreaseReplyCount(affectedRepliesCount);

        commentRepository.delete(findComment);
        findBoard.decreaseCommentCount();

        return CommentDeleteResponse.builder()
                .commentId(findComment.getId())
                .responseCode(ErrorCode.SUCCESS_COMMENT_DELETE.getDescription())
                .build();
    }
}
