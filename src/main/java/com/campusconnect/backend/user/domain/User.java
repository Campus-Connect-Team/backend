package com.campusconnect.backend.user.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 4)
    private String name;

    @Column(name = "student_number", nullable = false, unique = true, length = 8)
    private String studentNumber;

    @Column(name = "college", nullable = false, length = 10)
    private String college;

    @Column(name = "department", nullable = false, length = 10)
    private String department;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "image", length = 400)
    private String image;

    @Column(name = "buyer_manner")
    private Long buyerManner;

    @Column(name = "seller_manner")
    private Long sellerManner;
}
