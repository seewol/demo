package com.jeeeun.demo.config.data;

import com.jeeeun.demo.domain.product.Category;
import com.jeeeun.demo.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"local", "dev"})
// 스프링이 빈 등록 시 활성화된 프로필이 local 또는 dev 일 때만 DataInitializer 빈을 등록함
// prod(운영)은 보통 seed 금지로 운영
@Component
// Spring 시작 시 해당 클래스 찾아 자동으로 객체(Bean; 빈) 만들고 관리
@RequiredArgsConstructor
// final 필드를 받는 생성자를 Lombok 이 자동 생성
// Spring 이 CategoryRepository 주입 가능 (Dependency Injection; 의존성 주입)
public class DataInitializer implements ApplicationRunner {

    @PostConstruct
    public void check() {
        System.out.println("=== DataInitializer loaded ===");
    }
    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedCategories();
    }

    private void seedCategories() {
        // upsert = update + insert (있으면 수정, 없으면 생성)
        upsertCategory("Top");
        upsertCategory("Bottom");
        upsertCategory("Dress");
        upsertCategory("Acc");
    }

    private void upsertCategory(String name) {
        if (!categoryRepository.existsByName(name)) {
            categoryRepository.save(
                    Category.builder()
                            .name(name)
                            .build()
            );
        }
    }


}
