package com.campusconnect.backend.board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "board_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BoardImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "board_image", length = 255)
    private String boardImage;

    @Builder
    public BoardImage(Board board, String boardImage) {
        this.board = board;
        this.boardImage = boardImage;
    }

    public String getFileName() {
        return this.boardImage;
    }
}
