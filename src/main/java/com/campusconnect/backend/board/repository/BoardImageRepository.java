package com.campusconnect.backend.board.repository;

import com.campusconnect.backend.board.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {

    // 관련된 게시글 이미지까지 모두 삭제(DB)
    void deleteAllByBoardId(Long boardId);

    // 관련된 게시글 이미지를 모두 가져온다
    List<String> findByBoardId(Long boardId);
}
