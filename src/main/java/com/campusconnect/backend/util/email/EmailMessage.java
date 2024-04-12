package com.campusconnect.backend.util.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {

    private String to;       // 수신자
    private String title;    // 메일 제목
    private String message;  // 메일 내용
}
