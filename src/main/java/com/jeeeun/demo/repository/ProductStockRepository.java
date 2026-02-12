package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.product.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Integer> {

    Optional<ProductStock> findByProductVariant_Id(Integer id);
}
