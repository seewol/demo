package com.jeeeun.demo.repository.product;
import com.jeeeun.demo.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<Product> searchProducts(String keyword, Long categoryId, Boolean isDiscounted, Pageable pageable);
    // Page, Pageable: Spring Data JPA가 제공하는 페이징 전용 클래스

}
