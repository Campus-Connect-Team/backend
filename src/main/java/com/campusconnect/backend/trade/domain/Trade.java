package com.campusconnect.backend.trade.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import com.campusconnect.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trade")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Trade extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @Column(name = "buyer_manner_score")
    private Double buyerMannerScore;

    @Column(name = "seller_manner_score")
    private Double sellerMannerScore;
}
