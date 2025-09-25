package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {


}
