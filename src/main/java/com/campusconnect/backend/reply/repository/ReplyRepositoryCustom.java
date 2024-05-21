package com.campusconnect.backend.reply.repository;

import com.campusconnect.backend.reply.domain.Reply;

import java.util.List;
import java.util.Optional;

public interface ReplyRepositoryCustom {

    // 특정 댓글에 대한 답글을 모두 조회
    Optional<List<Reply>> findAllReplyByCommentId(Long commentId);

    // 본인이 작성한 답글이 맞는지 검증
    Boolean isReplyWrittenByRelevantUser(Long commentId, String studentNumber);

    // 댓글 삭제 시 댓글에 달려있던 답글도 모두 삭제
    void deleteAllByCommentId(Long commentId);

    // 게시글 삭제 시 해당 게시글과 관련된 댓글, 답글 내역도 모두 삭제
    void deleteAllByBoardId(Long boardId);

    // 회원탈퇴 시, 본인이 작성한 답글, 댓글 내역 모두 삭제
    void deleteAllByMyStudentNumber(String studentNumber);

    /** 특정 판매 게시글 조회 (게시글 상세 페이지) */
    Optional<List<Reply>> findAllRepliesByBoardId(Long boardId);
}
