package com.campusconnect.backend.board.dto.response;

import com.campusconnect.backend.util.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardUpdateResponse {

    private Long boardId;
    private ErrorCode errorCode;
}
