package com.jeeeun.demo.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 가입 방식 저장용 테이블

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    // 구글 로그인 = 구글 고유 ID (identifier와 같은 값)
    // 일반(로컬) 로그인 = null (필요 없으니까)
    @Column(name ="provider_id")
    private String providerId;

    // 로그인 식별자
    // 일반(로컬) = 이메일, 구글 = 구글 고유 ID
    @Column(name = "identifier")
    private String identifier;

    // LOCAL : 암호화된 비밀번호
    // GOOGLE : null
    @Column(name = "secret")
    private String secret;


}
