package com.campusconnect.backend.comment.repository;

import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.comment.domain.Comment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRepositoryCustom {

    /** 특정 게시글의 모든 댓글 리스트를 조회 */
    Optional<List<Comment>> findAllCommentByBoardId(Long boardId);

    /** 게시글의 특정 댓글을 조회 */
    Optional<Comment> findSingleCommentByBoardId(Long boardId, Long commentId);

    /** 댓글 수정 이전 선택한 댓글이 본인이 작성했는지 검증  */
    Boolean isCommentWrittenByRelevantUser(Long boardId, String studentNumber);

    /** 게시글 삭제 시 해당 게시글과 관련된 댓글, 답글 내역도 모두 삭제 */
    void deleteByAllBoardId(Long boardId);

    /** 회원탈퇴 시, 본인이 작성한 답글, 댓글 내역 모두 삭제 */
    Map<Long, Long> deleteByUserStudentNumberAndReturnBoardIds(String studentNumber);

    /** 작성한 댓글에 대한 판매자 게시글 리스트 영역 */
    /** 본인이 작성한 댓글 총 개수,작성한 댓글에 대한 판매 게시글 조회 */
    Integer findByStudentNumber(String studentNumber);
    List<Board> findBoardsByUserComments(String studentNumber);
}
