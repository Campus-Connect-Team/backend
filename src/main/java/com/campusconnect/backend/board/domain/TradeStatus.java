package com.campusconnect.backend.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeStatus {
    TRADE_ACTIVATION("거래 가능"),
    TRADE_COMPLETION("거래 완료");

    private String value;
}
