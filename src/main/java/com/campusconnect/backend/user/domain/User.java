package com.campusconnect.backend.user.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 5)
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
    private Double buyerManner;

    @Column(name = "seller_manner")
    private Double sellerManner;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Builder
    public User(Long id,
                String name,
                String studentNumber,
                String college,
                String department,
                String password,
                String email,
                String image,
                UserRole role
                ) {
        this.id = id;
        this.name = name;
        this.studentNumber = studentNumber;
        this.college = college;
        this.department = department;
        this.password = password;
        this.email = email;
        this.image = image;
        this.sellerManner = 0.0D;
        this.buyerManner = 0.0D;
        this.role = UserRole.USER;
    }
}
