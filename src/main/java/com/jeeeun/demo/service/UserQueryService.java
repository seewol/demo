package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.repository.UserRepository;
import com.jeeeun.demo.repository.ProductRepository;
import com.jeeeun.demo.service.user.model.UserDetailResult;
import com.jeeeun.demo.service.user.model.UserResult;
import com.jeeeun.demo.service.user.model.ProductSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 조회 전용 QueryService

@RequiredArgsConstructor
@Service
public class UserQueryService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 회원 목록 조회에 대한 api 생성
    @Transactional(readOnly = true)
    public List<UserResult> getUsers() {
        return userRepository.findAllByIsDeletedFalse()
                .stream()
                // map() : User 엔티티 → UserResult 로 변환
                .map(UserResult::from)
                // user -> UserResult.from(user) 와 동일
                // UserResult 안의 from 메서드를 함수로 사용한다는 의미
                .toList();
    }

    // 회원 단일 조회에 대한 api 생성 (내 정보 조회)
    @Transactional(readOnly = true)
    public UserDetailResult getUserDetail(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        ProductSummaryResult ps = productRepository.findMainSummaryByUserId(userId)
                .orElse(null); // 상품 없으면 ps 가 null

        return UserDetailResult.from(user, ps);
    }
}