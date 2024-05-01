package com.campusconnect.backend.user.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Slf4j
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

    /** 마이 페이지 - 기본 프로필 수정 */
    public void editMyBasicProfile(String college,
                                   String department,
                                   String name) {
        this.college = college;
        this.department = department;
        this.name = name;
    }

    /** 프로필 이미지를 제외한 나머지 수정의 경우 MODIFIED_DATE 최신화 */
    public void editModifiedDate() {
        this.setModifiedDate(LocalDateTime.now());
    }

    /** 정보 수정 시 프로필 이미지를 고르지 않았다면 기본 이미지 설정 */
    public void setProfileImageToBasicImage() {
        this.image = UserImageInitializer.getDefaultImageUrl();
    }

    public void editProfileImage(String imageUrl) {
        this.image = imageUrl;
    }
}
