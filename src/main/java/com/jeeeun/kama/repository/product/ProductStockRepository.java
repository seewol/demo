package com.jeeeun.kama.repository.product;

import com.jeeeun.kama.domain.product.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

    Optional<ProductStock> findByProductVariant_Id(Long id);
}
