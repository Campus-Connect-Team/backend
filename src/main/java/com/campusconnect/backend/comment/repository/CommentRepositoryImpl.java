package com.campusconnect.backend.comment.repository;

import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.domain.QBoard;
import com.campusconnect.backend.comment.domain.Comment;
import com.campusconnect.backend.comment.domain.QComment;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.campusconnect.backend.board.domain.QBoard.board;
import static com.campusconnect.backend.comment.domain.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /** 특정 게시글의 모든 댓글 리스트를 조회 */
    @Override
    public Optional<List<Comment>> findAllCommentByBoardId(Long boardId) {
        return Optional.ofNullable(queryFactory
                .select(comment)
                .from(comment)
                .where(comment.board.id.eq(boardId))
                .fetch());
    }

    /** 게시글의 특정 댓글을 조회 */
    @Override
    public Optional<Comment> findSingleCommentByBoardId(Long boardId, Long commentId) {
        return Optional.ofNullable(queryFactory
                .select(comment)
                .from(comment)
                .where(comment.board.id.eq(boardId),
                        comment.id.eq(commentId))
                .fetchOne());
    }

    /** 댓글 수정 이전, 선택한 댓글이 본인이 작성했는지 검증 */
    @Override
    public Boolean isCommentWrittenByRelevantUser(Long boardId, String studentNumber) {
        return queryFactory
                .select(comment)
                .from(comment)
                .where(comment.board.id.eq(boardId),
                        comment.user.studentNumber.eq(studentNumber))
                .fetchFirst() != null;
    }

    /** 게시글 삭제 시 해당 게시글과 관련된 댓글, 답글 내역도 모두 삭제 */
    @Override
    public void deleteByAllBoardId(Long boardId) {
        queryFactory
                .delete(comment)
                .where(comment.board.id.eq(boardId))
                .execute();
    }

    /** 회원탈퇴 시, 본인이 작성한 답글, 댓글 내역 모두 삭제 */
    @Override
    public Map<Long, Long> deleteByUserStudentNumberAndReturnBoardIds(String studentNumber) {
        // 삭제할 댓글과 관련된 게시글 ID와 삭제될 댓글 수 조회
        List<Tuple> affectedBoardIdsWithCount = queryFactory
                .select(comment.board.id, comment.count())
                .from(comment)
                .where(comment.user.studentNumber.eq(studentNumber))
                .groupBy(comment.board.id)
                .fetch();

        // 댓글 삭제
        queryFactory
                .delete(comment)
                .where(comment.user.studentNumber.eq(studentNumber))
                .execute();

        // 결과를 Map으로 반환
        return affectedBoardIdsWithCount.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.board.id),
                        tuple -> tuple.get(comment.count())
                ));
    }

    /** 작성한 댓글에 대한 판매자 게시글 리스트 영역 */
    @Override
    public Integer findByStudentNumber(String studentNumber) {
        List<Comment> comments = queryFactory
                .select(comment)
                .from(comment)
                .where(comment.commenterStudentNumber.eq(studentNumber))
                .fetch();
        return comments.size();
    }

    /** 본인이 작성한 댓글에 대한 판매 게시글 조회 */
    @Override
    public List<Board> findBoardsByUserComments(String studentNumber) {
        return queryFactory
                .select(comment.board)
                .distinct()
                .from(comment)
                .join(comment.board, board)
                .where(comment.user.studentNumber.eq(studentNumber))
                .fetch();
    }
}
