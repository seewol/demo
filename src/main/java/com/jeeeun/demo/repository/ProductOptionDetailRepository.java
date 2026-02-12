package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.product.ProductOptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductOptionDetailRepository extends JpaRepository<ProductOptionDetail, Integer>, ProductOptionDetailRepositoryCustom {

    @Query("""
        select d from ProductOptionDetail d
        where d.productOption.product.id = :productId
        order by d.productOption.id asc, d.id asc
    """)
    List<ProductOptionDetail> findAllByProductId(Integer productId);

}
