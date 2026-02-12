package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.product.Product;
import com.jeeeun.demo.service.member.model.ProductSummaryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductRepositoryCustom {

    // @Query 는 Spring Data JPA 에서
    // 메서드 이름 기반 쿼리 생성 대신,
    // 직접 작성한 JPQL(엔티티 기준)을 사용한다.

    @Query("""
       select new com.jeeeun.demo.service.member.model.ProductSummaryResult(
            p.id,
            p.name,
            p.salePrice,
            p.isDiscounted,
            p.discountRate
       )
       from Product p
       where p.member.id = :memberId and p.isDeleted = false
       order by p.createdAt desc
    """)
    Optional<ProductSummaryResult> findMainSummaryByMemberId(Integer memberId); // :memberId 와 자동 매핑

    @Query("select p from Product p join fetch p.category")
    List<Product> findAllByCategory();

    // Optional = 값이 있으면 값 꺼냄 (없으면 예외 발생, orElseThrow())

    // ProductOption, ProductOptionDetail, ProductImage 쿼리 쪼개기
    // ...by : 조건으로 검색
    // ...with : 함께 로딩 (fetch)

    @Query("""
        select distinct p from Product p
        join fetch p.category
        left join fetch p.productOptions po
        where p.id = :productId and p.isDeleted = false
    """) // 실무에서는 fetch join + distinct(중복 제거)를 거의 세트로 씀
    Optional<Product> findDetailWithOptions(Integer productId);

}
