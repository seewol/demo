package com.jeeeun.demo.domain.user;

import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import com.jeeeun.demo.controller.request.LocalSignInRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 512)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // DTO든 엔티티든 상관없이 '객체 생성' 책임을
    // 해당 클래스 안으로 숨기는 게 중요함.
    // from() 은 DTO 전용이 아닌 '생성 패턴'
    public static UserToken from(User user, String refreshToken, LocalDateTime expiresAt) {
        return UserToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .build();
    }

}
