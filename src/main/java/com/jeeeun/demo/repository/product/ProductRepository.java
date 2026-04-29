package com.jeeeun.demo.repository.product;

import com.jeeeun.demo.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    // @Query 는 Spring Data JPA 에서
    // 메서드 이름 기반 쿼리 생성 대신,
    // 직접 작성한 JPQL(엔티티 기준)을 사용한다.

    @Query("select p from Product p join fetch p.category")
    List<Product> findAllByCategory();

    // Optional = 값이 있으면 값 꺼냄 (없으면 예외 발생, orElseThrow())

    // ...by : 조건으로 검색
    // ...with : 함께 로딩 (fetch)


    @Query("""
        select distinct p from Product p
        join fetch p.category
        left join fetch p.productOptions po
        where p.id = :productId and p.isDeleted = false
    """) // 실무에서는 fetch join + distinct(중복 제거)를 거의 세트로 씀
    Optional<Product> findDetailWithOptions(Long productId);


    // 할인 기간 지난 상품 isDiscounted = false 업데이트
    // (종료일 < 현재 시각)
    @Modifying
    @Query("""
            update Product p
            set p.isDiscounted = false
            where p.isDiscounted = true
            and p.discountEndAt < :now
            """)
    int bulkExpireDiscounts(@Param("now") LocalDateTime now);


    // 할인 기간 시작된 상품 isDiscounted = true 업데이트
    // 시작일 <= 현재 시각 <= 종료일
    @Modifying
    @Query("""
            update Product p
            set p.isDiscounted = true
            where p.isDiscounted = false
            and p.discountStartAt <= :now
            and p.discountEndAt >= :now
            and p.discountRate is not null
            """)
    int bulkStartDiscounts(@Param("now") LocalDateTime now);


}
