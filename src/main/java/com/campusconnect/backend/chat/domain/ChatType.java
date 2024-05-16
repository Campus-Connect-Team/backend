package com.campusconnect.backend.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatType {

    ENTER("USER_ENTRANCE"),
    TALK("USER_CHATTING"),
    LEAVE("USER_LEAVED");

    private String value;
}
