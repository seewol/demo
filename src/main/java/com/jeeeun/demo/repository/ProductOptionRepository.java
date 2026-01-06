package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Integer> {
}
