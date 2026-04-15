package com.jeeeun.demo.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

    // DB ←→ EntityManager(중간 통역사) ←→ 내 코드
    @PersistenceContext
    private EntityManager entityManager;

    // Querydsl이 쿼리를 만들고, EntityManager에 실행을 맡기는 구조!
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

}
