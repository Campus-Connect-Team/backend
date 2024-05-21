package com.campusconnect.backend.comment.repository;

import com.campusconnect.backend.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    // 특정 게시글에 대해 본인이 작성한 댓글의 총 개수를 반환
    Integer countByBoardIdAndUserStudentNumber(Long boardId, String studentNumber);

}
