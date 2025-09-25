package com.jeeeun.demo;

import com.jeeeun.demo.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@EnableJpaAuditing
@SpringBootApplication
public class JpaTest {

    public static void main(String[] args){

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");

        // EntityManagerFactory를 통해 EntityManager를 만듦
        EntityManager em = emf.createEntityManager();
        // └ 각 트랜잭션 단위로 새로 생성 or 닫으며 영속성 컨테스트를 가짐

        // EntityManager를 통해 transaction을 만듦.
        EntityTransaction tx = em.getTransaction();

        // 성공 or 실패 가능성이 있어 try-catch 문 사용
        try {
            tx.begin();
            // 1. 비영속 상태
            Member member = new Member();
            member.setMemberName("박지은");
            member.setMemberPw("jeeeun12");
            member.setMemberEmail("jeeneepark@naver.com");
            member.setPhoneNumber("01038561226");
            member.setCreatedAt(LocalDateTime.now());
            member.setUpdatedAt(LocalDateTime.now());

            // 2. 영속 상태, 1차 캐시에 진입 (아직 DB에 insert는 안 됨)
            em.persist(member);

            // 3. 1차에서 캐시 조회
            Member found = em.find(Member.class, 1);
            System.out.println(member == found); // 트랜잭션, 컨텍스트 내 동일성(==) 보장
            System.out.println("메롱");

            // 4. Dirth Cheking (변경 감지)
            found.setMemberName("JEEEUN");

            // 5. 플러시 : 트랜잭션 커밋 시 자동 flush
            em.flush();
            tx.commit(); // 자동 flush, commit 시점에 한 번에 적용되도록 DML 모으기

        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback(); // 실패 시
        } finally {
            em.close(); // 새로 생성하면 작업이 끝났으니 닫아줌.
        }

    }
}
