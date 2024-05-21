package com.campusconnect.backend.reply.repository;

import com.campusconnect.backend.reply.domain.QReply;
import com.campusconnect.backend.reply.domain.Reply;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.campusconnect.backend.reply.domain.QReply.*;

@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 특정 댓글에 대한 답글을 모두 조회
    @Override
    public Optional<List<Reply>> findAllReplyByCommentId(Long commentId) {
        return Optional.ofNullable(queryFactory
                .select(reply)
                .from(reply)
                .where(reply.comment.id.eq(commentId))
                .fetch());
    }

    // 답글 수정 이전, 본인이 작성한 답글이 맞는지 검증
    @Override
    public Boolean isReplyWrittenByRelevantUser(Long commentId, String studentNumber) {
        return queryFactory
                .select(reply)
                .from(reply)
                .where(reply.comment.id.eq(commentId),
                        reply.replierStudentNumber.eq(studentNumber))
                .fetchFirst() != null;
    }

    // 댓글 삭제 시 댓글에 달려있던 답글도 모두 삭제
    @Override
    public void deleteAllByCommentId(Long commentId) {
        queryFactory
                .delete(reply)
                .where(reply.comment.id.eq(commentId))
                .execute();
    }

    // 게시글 삭제 시 해당 게시글과 관련된 댓글, 답글 내역도 모두 삭제
    @Override
    public void deleteAllByBoardId(Long boardId) {
        queryFactory
                .delete(reply)
                .where(reply.comment.board.id.eq(boardId))
                .execute();
    }

    // 회원탈퇴 시, 본인이 작성한 답글, 댓글 내역 모두 삭제
    @Override
    public void deleteAllByMyStudentNumber(String studentNumber) {
        queryFactory
                .delete(reply)
                .where(reply.user.studentNumber.eq(studentNumber))
                .execute();
    }

    /** 특정 판매 게시글 조회 (게시글 상세 페이지) */
    @Override
    public Optional<List<Reply>> findAllRepliesByBoardId(Long boardId) {
        return Optional.ofNullable(queryFactory
                .select(reply)
                .from(reply)
                .where(reply.comment.board.id.eq(boardId))
                .fetch());
    }
}
