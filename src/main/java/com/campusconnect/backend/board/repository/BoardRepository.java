package com.campusconnect.backend.board.repository;

import com.campusconnect.backend.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    // 페이징 처리
    Page<Board> findAll(Pageable pageable);
}
