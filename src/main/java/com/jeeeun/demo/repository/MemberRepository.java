package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer>, MemberRepositoryCustom {
    boolean existsByMemberEmailAndIsDeletedFalse(String email);

    boolean existsByPhoneNumberAndIsDeletedFalse(String phoneNumber);

    List<Member> findAllByIsDeletedFalse();

    // Optional = 값이 있으면 값 꺼냄 (없으면 예외 발생, orElseThrow())
    Optional<Member> findByMemberIdAndIsDeletedFalse(Integer memberId);


    // <1, 2> 제네릭 타입 안의 1은 다룰 Entity, 2는 Entity pk type
    // JpaRepository → CRUD 기능이 다 들어있음!

    // JpaRepository 기본 구성
    // .findById(id) : id로 Member 찾기
    // .findAll() : Member 전체 조회
    // .save(new Member("name")) : Member 객체 생성 (회원가입)
    // .deleteById(id) : id로 Member 삭제

    // 커스텀 구성
    // (복잡한 쿼리, 동적인 조건을 검색해야 하는 경우)

}
