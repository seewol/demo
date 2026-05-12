package com.jeeeun.demo.repository.product;

import com.jeeeun.demo.domain.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("""
        select pi from ProductImage pi
        where pi.product.id = :productId
        order by pi.id asc
    """)
    List<ProductImage> findAllByProductId(Long productId);

    @Query("""
        select pi from ProductImage pi
        where pi.product.id = :productId
            and pi.imageOrder = 1
    """)
    Optional<ProductImage> findThumbnailByProductId(Long productId);
}
