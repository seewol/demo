package com.jeeeun.kama.repository.product;

import com.jeeeun.kama.domain.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

}
