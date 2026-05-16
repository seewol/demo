package com.jeeeun.demo.domain.user;

import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

// user Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    @Column(name = "user_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<UserCredentials> credentials = new ArrayList<>();

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "user_email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)    // DB에 "USER" 처럼 문자열로 저장
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;  // 회원가입하면 자동으로 일반 유저

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;


    // from()으로만 생성 강제!
    // name, email, phoneNumber 세 개 다 있어야 User 만들 수 있음
    public static User from(String name, String email, String phoneNumber) {
        User user = new User();
        user.name = name;
        user.email = email;
        user.phoneNumber = phoneNumber;
        return user;
    }

    // 내 정보 수정
    public void updateProfile(String name, String phoneNumber) {
        if (hasText(name)) {
            this.name = name.trim();
        }
        if (hasText(phoneNumber)) {
            this.phoneNumber = phoneNumber.trim();
        }
    }

    // 회원 삭제
    public void withdraw() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.email = null;
        this.phoneNumber = null;
    }
}
