package com.jeeeun.demo.domain.user;

import jakarta.persistence.*;
import lombok.*;

// 가입 방식 저장용 테이블

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_credentials",
    uniqueConstraints = @UniqueConstraint(columnNames = {"identifier", "provider"}))
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credentials_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 가입 방식 (ex. "LOCAL", "GOOGLE" → DB에 문자열로 저장)
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private Provider provider;

    // 로그인 식별자
    // 일반(로컬) = 이메일, 구글 = 구글 고유 ID
    @Column(name = "identifier")
    private String identifier;

    // LOCAL : 암호화된 비밀번호
    // GOOGLE : null
    @Column(name = "secret")
    private String secret;


    // 로컬 로그인용
    public static UserCredentials ofLocal(User user, String identifier, String secret) {
        UserCredentials uc = new UserCredentials();
        uc.user = user;
        uc.provider = Provider.LOCAL;
        uc.identifier = identifier;
        uc.secret = secret;
        return uc;
    }

    // 구글 로그인용
    public static UserCredentials ofGoogle(User user, String googleId) {
        UserCredentials uc = new UserCredentials();
        uc.user = user;
        uc.provider = Provider.GOOGLE;
        uc.identifier = googleId;
        uc.secret = null;
        return uc;
    }
}
