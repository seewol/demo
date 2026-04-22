package com.jeeeun.demo.repository.user;

import com.jeeeun.demo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByPhoneNumberAndIsDeletedFalse(String phoneNumber);

    List<User> findAllByIsDeletedFalse();

    // Optional = 값이 있으면 값 꺼냄 (없으면 예외 발생, orElseThrow())
    Optional<User> findByIdAndIsDeletedFalse(Long UserId);

    boolean existsByPhoneNumberAndIsDeletedFalseAndIdNot(String newPhone, Long id);

    // 로그인 용
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    // <1, 2> 제네릭 타입 안의 1은 다룰 Entity, 2는 Entity pk type
    // JpaRepository → CRUD 기능이 다 들어있음!

    // JpaRepository 기본 구성
    // .findById(id) : id로 User 찾기
    // .findAll() : User 전체 조회
    // .save(new User("name")) : User 객체 생성 (회원가입)
    // .deleteById(id) : id로 User 삭제

    // 커스텀 구성
    // (복잡한 쿼리, 동적인 조건을 검색해야 하는 경우)

}
