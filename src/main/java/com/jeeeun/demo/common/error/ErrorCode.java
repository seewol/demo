package com.jeeeun.demo.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INTERNAL_SERVER_ERROR (HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 유저
    CONFLICT_USER(HttpStatus.CONFLICT, "이미 등록된 사용자입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "등록되지 않은 사용자입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "이미 사용 중인 번호입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "번호 형식이 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "밀번호가 올바르지 않습니다."),

    // 상품
    CONFLICT_PRODUCT(HttpStatus.CONFLICT, "중복된 상품명입니다."),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "등록되지 않은 상품입니다."),

    // 할인
    INVALID_DISCOUNT_RATE(HttpStatus.BAD_REQUEST, "할인 적용 시 할인율은 필수입니다."),
    INVALID_DISCOUNT_PERIOD(HttpStatus.BAD_REQUEST, "할인 기간이 올바르지 않습니다."),

    // 주문
    CONFLICT_ORDER(HttpStatus.CONFLICT, "중복된 주문 요청입니다."),
    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "존재하지 않는 주문 정보입니다."),

    // 재고
    OUT_OF_STOCK(HttpStatus.NOT_FOUND, "상품 재고가 부족합니다."),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "재고 수량은 0보다 커야 합니다."),

    // 카테고리
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),

    // 옵션
    NOT_FOUND_OPTION(HttpStatus.NOT_FOUND, "존재하지 않는 옵션입니다."),
    INVALID_OPTION_DETAIL_IDS(HttpStatus.BAD_REQUEST, "옵션 디테일 ID 목록이 유효하지 않습니다."),
    OPTION_DETAIL_REQUIRED(HttpStatus.BAD_REQUEST, "옵션 디테일이 유효하지 않습니다."),

    // 조합
    NOT_FOUND_VARIANT(HttpStatus.NOT_FOUND, "존재하지 않는 조합입니다.")
    ;

    private final HttpStatus status;
    private final String message;
}


// HttpStatus 코드 정리
/**
 * 200 OK          요청이 성공적으로 처리됨
 * 201 CREATED     요청이 성공적으로 처리되어 리소스가 생성됨
 * 204 NO_CONTENT  요청 성공, 응답 본문 없음 (DELETE 성공 시 주로 사용)
 * 400 BAD_REQUEST 잘못된 요청 (파라미터, JSON 형식 오류 등)
 * 404 NOT_FOUND   요청한 리소스를 찾을 수 없음
 * 409 CONFLICT    리소스 상태 충돌 (예: 중복 데이터 등)
 * 500 INTERNAL_SERVER_ERROR  서버 내부 오류 (예외 발생 등)
 *  **/

