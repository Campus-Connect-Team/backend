package com.campusconnect.backend.authentication.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authentication")
@Getter
@NoArgsConstructor
public class Authentication extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authentication_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "authentication_number")
    private String authenticationNumber;

    public Authentication(String email, String authenticationNumber) {
        this.email = email;
        this.authenticationNumber = authenticationNumber;
    }
}
