package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.UserCreateRequest;
import com.jeeeun.demo.controller.request.UserUpdateRequest;
import com.jeeeun.demo.controller.response.*;
import com.jeeeun.demo.service.UserCommandService;
import com.jeeeun.demo.service.UserQueryService;
import com.jeeeun.demo.service.user.model.UserCreateResult;
import com.jeeeun.demo.service.user.model.UserResult;
import com.jeeeun.demo.service.user.model.UserUpdateResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor // final 붙은 애들만 생성자 만들어 줌.
@RestController
@Tag(name = "UserController", description = "User CRUD API 엔드포인트")
// API 엔드 포인트 담당
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;


    /*
        -- 전체 흐름
        클라이언트 → [Controller] → [Service] → [Repository] → DB(user 테이블)
                         ↑                                      ↓
                      [JSON]  ←  [UserResponse DTO]  ←  [User Entity]
     */


    // 회원 가입 (C)
    @Operation(summary = "회원 가입", description = "회원을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원 등록 성공")
    @PostMapping("/sign-up")
    public UserCreateResponse signUp(
            @Valid @RequestBody UserCreateRequest request
    ) {

        UserCreateResult result = userCommandService.signUp(request.toCommand());

        return UserCreateResponse.from(result);
    }


    // 멤버 목록 조회 (R)
    @Operation(summary = "회원 목록 조회", description = "회원 전체 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/users")
    public List<UserResponse> getUsers() {

        List<UserResult> results = userQueryService.getUsers();

        // List<UserResult> → List<UserResponse> 변환 과정
        return results.stream()
                .map(UserResponse::from) // 람다식
                // result -> UserResponse.from(result) 와 동일 (result 변수명은 큰 의미 없음)
                // 스트림의 각 UserResult 를 하나씩 꺼내서 UserResponse 로 변환
                .toList();

    }


    // 내 정보 조회 (회원 상세 조회) (R)
    // 토큰에서 userId 꺼내기
    @Operation(summary = "내 정보 조회", description = "회원 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/users/me")
    public UserDetailResponse getUser() {

        // SecurityContext 안에서 현재 로그인한 사람의 userId 꺼내기
        Long userId = (Long) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

        return UserDetailResponse.from(userQueryService.getUserDetail(userId));

    }


    // 내 정보 수정 (U)
    @Operation(summary = "내 정보 수정", description = "회원 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/users/me")
    public UserUpdateResponse updateUser(
            @Valid @RequestBody UserUpdateRequest request
    ) {

        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // request.validate(); 대신 toCommand() 내에서 검증
        UserUpdateResult result = userCommandService.updateUser(request.toCommand(userId));

        return UserUpdateResponse.from(result);
    }

    /*
        HTTP
         ├─ PathVariable(userId)  ← 리소스 식별
         └─ Request Body          ← 수정 데이터
                ↓
        Controller 에서 합쳐서
                ↓
        Command (유스케이스 입력 모델)
                ↓
        Service / UseCase
     */

    // 회원 탈퇴 (D)
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.")
    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
    @DeleteMapping("/users/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
    ) {

        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        userCommandService.deleteUser(userId);
    }

}
