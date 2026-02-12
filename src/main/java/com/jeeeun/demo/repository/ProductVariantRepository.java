package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

}
