package com.campusconnect.backend.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentCreateRequest {

    @NotEmpty(message = "댓글 내용을 입력하셔야 합니다.")
    @Size(max = 100, message = "댓글은 최대 100자까지 작성하실 수 있습니다.")
    private String commentContent;

    @Builder
    public CommentCreateRequest(String commentContent) {
        this.commentContent = commentContent;
    }
}
