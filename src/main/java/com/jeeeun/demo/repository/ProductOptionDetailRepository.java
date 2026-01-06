package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.ProductOptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionDetailRepository extends JpaRepository<ProductOptionDetail, Integer>, ProductOptionDetailRepositoryCustom {


}
