package com.jeeeun.demo.domain.member;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// member Entity
@Builder // 필드가 많을 때 가독성적으로 좋아서 Setter 대신 사용.
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "member_email", nullable = false, unique = true)
    private String memberEmail;

    @Column(name = "member_pw", nullable = false)
    private String memberPw;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name= "updated_at")
    private LocalDateTime updatedAt;
}
