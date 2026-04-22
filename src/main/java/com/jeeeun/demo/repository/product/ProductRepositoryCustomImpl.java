package com.jeeeun.demo.repository.product;

import com.jeeeun.demo.domain.product.Product;
import com.jeeeun.demo.domain.product.QCategory;
import com.jeeeun.demo.domain.product.QProduct;
import com.jeeeun.demo.repository.product.ProductRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    // Querydsl 쿼리 실제로 실행하는 핵심 객체
    // Bean 등록해야 주입 가능
    private final JPAQueryFactory queryFactory;

    // Q클래스 : 엔티티를 Querydsl가 이해할 수 있는 형태로 변환한 것
    // QProduct.product → Product 테이블 가리키는 Querydsl 객체
    private final QProduct p = QProduct.product;
    private final QCategory c = QCategory.category;
    // 각각의 p, c는 SQL 쿼리 안에서 쓰이는 별칭과 같은 개념

    @Override
    public Page<Product> searchProducts(String keyword, Long categoryId, Boolean isDiscounted, Pageable pageable) {

        // 실제 데이터 조회
        List<Product> contents = queryFactory
                .selectFrom(p)                      // SELECT * FROM product p
                .join(p.category, c).fetchJoin()    // JOIN category c (fetchJoin → 한 번에 같이 가져옴)
                .where(
                        notDeleted(),               // 삭제 안 된 것만!
                        containsKeyword(keyword),   // 키워드 검색   (셋 다 null이면 무시)
                        eqCategory(categoryId),     // 카테고리 필터
                        eqDiscounted(isDiscounted)  // 할인 여부 필터
                )
                .offset(pageable.getOffset())       // 몇 번째부터 (ex. 3페이지면 30번째부터)
                .limit(pageable.getPageSize())      // 몇 개씩 (ex. 20개씩)
                .fetch();                           // 쿼리 실행 → List 반환

        // 전체 개수 조회 (페이징 계산용)
        Long total = queryFactory
                .select(p.count())                  // SELECT COUNT(*)
                .from(p)
                .where(
                        notDeleted(),
                        containsKeyword(keyword),
                        eqCategory(categoryId),
                        eqDiscounted(isDiscounted)
                )
                .fetchOne();                        // 쿼리 실행 → 단건 반환

        return new PageImpl<>(contents, pageable, total != null ? total : 0);
        // PageImpl : Page의 구현체 (Page는 인터페이스)
        // contents(데이터) + pageable(페이징 정보) + total(전체 개수)
        // total이 null일 수 있으므로 0으로 처리했음.

        // ex)
        // contents = ["운동화", "구두", "부츠", ...] → 필터 거친 상품 1페이지 20개 담음
        // pageable = { page: 1, size: 20 }       → 1 페이지당, 20개씩 배치하겠다.
        // total = 30                             → 조건에 맞는 전체 상품 수
    }

    // BooleanExpression : true or false 조건식
    // Querydsl은 null을 반한하는 조건을 자동으로 무시한다.

    // 1. 삭제되지 않은 상품만 (항상 적용함)
    private BooleanExpression notDeleted() {
        return p.isDeleted.eq(false);   // WHERE is_deleted = false
    }

    // 2. 키워드 검색 : keyword가 null이면 조건 무시 ↔ 있으면 상품명 내 포함 여부 확인
    private BooleanExpression containsKeyword(String keyword) {
        return keyword != null ? p.name.contains(keyword) : null;
        // keyword = "보울" → WHERE product_name LIKE '%보울%'
        // keyword = null → 조건 없음 (전체 조회)
    }

    // 3. 카테고리 필터 : categoryId가 null이면 조건 무시
    private BooleanExpression eqCategory(Long categoryId) {
        return categoryId != null ? c.id.eq(categoryId) : null;
        // categoryId = 1L → WHERE category_id = 1
        // categoryId = null → 조건 없음
    }

    // 4. 할인 여부 필터 : isDiscounted가 null이면 조건 무시
    private BooleanExpression eqDiscounted(Boolean isDiscounted) {
        return isDiscounted != null ? p.isDiscounted.eq(isDiscounted) : null;
        // isDiscounted = true → WHERE is_discounted = true
        // isDiscounted = null → 할인 조건 없음
    }

}