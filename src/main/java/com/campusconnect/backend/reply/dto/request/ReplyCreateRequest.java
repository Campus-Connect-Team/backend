package com.campusconnect.backend.reply.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReplyCreateRequest {

    @NotEmpty(message = "답글 내용을 입력하셔야 합니다.")
    @Size(max = 100, message = "답글은 최대 100자까지 작성하실 수 있습니다.")
    private String replyContent;

    @Builder
    public ReplyCreateRequest(String replyContent) {
        this.replyContent = replyContent;
    }
}