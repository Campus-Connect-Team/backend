package com.campusconnect.backend.reply.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ReplyDeleteResponse {

    private Long replyId;
    private String responseCode;

    @Builder
    public ReplyDeleteResponse(Long replyId, String responseCode) {
        this.replyId = replyId;
        this.responseCode = responseCode;
    }
}
