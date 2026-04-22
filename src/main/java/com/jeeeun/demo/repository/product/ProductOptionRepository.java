package com.jeeeun.demo.repository.product;

import com.jeeeun.demo.domain.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
}
