package com.campusconnect.backend.board.repository;

import com.campusconnect.backend.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    // 삭제한 게시글을 기준으로 해당 게시글보다 높은 boardId를 가져온다.
    List<Board> findByIdGreaterThan(Long boarId);
}
