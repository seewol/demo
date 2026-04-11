package com.jeeeun.demo.domain.user;

import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

// user Entity
@Builder // 필드가 많을 때 가독성적으로 좋아서 Setter 대신 사용.
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    @Column(name = "user_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "user")
    @Builder.Default // 빌더로 생성해도 null 대신 빈 리스트 보장
    private List<UserCredentials> credentials = new ArrayList<>();

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "user_email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Builder.Default                // 빌더로 생성 시에도 기본값 USER 보장
    @Enumerated(EnumType.STRING)    // DB에 "USER" 처럼 문자열로 저장
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;  // 회원가입하면 자동으로 일반 유저

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

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
