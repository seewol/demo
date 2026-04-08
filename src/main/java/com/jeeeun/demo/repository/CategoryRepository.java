package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// @Repository → JpaRepository 상속 시 없어도 됨
// : Spring Data JPA가 자동으로 빈 등록해주기 때문!
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

}
