package com.campusconnect.backend.board.repository;

import com.campusconnect.backend.board.domain.Board;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardRepositoryCustom {

    // 게시글 검색(학과, 게시글 제목)
    List<Board> searchBoardWithSearchCond(String department, String title, Pageable pageable);
}
