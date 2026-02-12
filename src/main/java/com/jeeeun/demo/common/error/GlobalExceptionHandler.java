package com.jeeeun.demo.common.error;

import com.jeeeun.demo.util.exception.InvalidTokenException;
import com.jeeeun.demo.util.exception.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// 전역 예외처리 클래스
@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j // Logger 객체 자동 생성 -> log.info() / log.error() 사용하게 하는 어노테이션
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // JWT 토큰 만료
    @ExceptionHandler(TokenExpiredException.class)
    protected ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException e) {
                // └ HTTP 상태 코드 + 응답 바디 같이 주겠다는 의미

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .build();
        // 예외 안의 메세지를 꺼내 API 응답용 DTO(ErrorResponse)로 감쌈

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // JWT 토큰이 유효하지 않음
    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // 비즈니스 익셉션
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    // 인터널 익셉션
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}