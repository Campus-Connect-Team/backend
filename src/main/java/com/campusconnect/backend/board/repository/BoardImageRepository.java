package com.campusconnect.backend.board.repository;

import com.campusconnect.backend.board.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {

}
