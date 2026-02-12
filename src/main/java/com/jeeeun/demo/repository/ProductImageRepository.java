package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    @Query("""
        select pi from ProductImage pi
        where pi.product.id = :productId
        order by pi.id asc
    """)
    List<ProductImage> findAllByProductId(Integer productId);
}
