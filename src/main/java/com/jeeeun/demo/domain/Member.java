package com.jeeeun.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// member Entity
@Builder // 필드가 많을 때 가독성적으로 좋아서 Setter 대신 사용.
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"products"})
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

    @CreatedDate // 엔티티 최초 저장(insert) 시에만 자동으로 값 채워짐
    @Column(name = "created_at", updatable = false) // 이후 update 시 DB 반영 안 됨
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 변경(update) 될 떄마다 자동 갱신, insert 때도 한 번 세팅
    @Column(name= "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member")
    private List<Product> products = new ArrayList<>();
}
