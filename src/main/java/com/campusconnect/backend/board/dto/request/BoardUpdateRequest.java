package com.campusconnect.backend.board.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BoardUpdateRequest {

    @NotEmpty(message = "게시글 제목 입력은 필수 사항입니다.")
    @Size(max = 100, message = "게시글 제목은 최대 100자까지 작성할 수 있습니다.")
    private String title;

    @NotEmpty(message = "게시글 내용 입력은 필수 사항입니다.")
    @Size(max = 1000, message = "게시글 제목은 최대 1,000자까지 작성할 수 있습니다.")
    private String content;

    private String tradeStatus;

    private List<Long> deletedImages;
}
