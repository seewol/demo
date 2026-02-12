package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.MemberCreateRequest;
import com.jeeeun.demo.controller.request.MemberUpdateRequest;
import com.jeeeun.demo.controller.request.UserLoginRequest;
import com.jeeeun.demo.controller.response.*;
//import com.jeeeun.demo.service.AuthService;
import com.jeeeun.demo.service.MemberCommandService;
import com.jeeeun.demo.service.MemberQueryService;
import com.jeeeun.demo.service.member.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor // final 붙은 애들만 생성자 만들어 줌.
@RestController
@Tag(name = "MemberController", description = "Member CRUD API 엔드포인트")
// API 엔드 포인트 담당
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
//    private final AuthService authService; // 인증만 다루는 서비스


    /*
        -- 전체 흐름
        클라이언트 → [Controller] → [Service] → [Repository] → DB(member 테이블)
                         ↑                                      ↓
                      [JSON]  ←  [MemberResponse DTO]  ←  [Member Entity]
     */


    // 로그인
//    @Operation(summary = "로그인", description = "회원 로그인")
//    @ApiResponse(responseCode = "201", description = "회원 로그인 성공")
//    @PostMapping("/login")
//    public void loginUser(
//            @Valid @RequestBody UserLoginRequest request) {
//        = authService.login(request.toCommand());
//    }


    // 회원 가입 (C)
    @Operation(summary = "회원 가입", description = "회원을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원 등록 성공")
    @PostMapping("/signUp")
    public MemberCreateResponse signUp(
            @Valid @RequestBody MemberCreateRequest request
    ) {

//        request.validate();

        MemberCreateResult result = memberCommandService.signUp(request.toCommand());

        return MemberCreateResponse.from(result);
    }


    // 멤버 목록 조회 (R)
    @Operation(summary = "회원 목록 조회", description = "회원 전체 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/members")
    public List<MemberResponse> getMembers() {

        List<MemberResult> results = memberQueryService.getMembers();

        // List<MemberResult> → List<MemberResponse> 변환 과정
        return results.stream()
                .map(MemberResponse::from) // 람다식
                // result -> MemberResponse.from(result) 와 동일 (result 변수명은 큰 의미 없음)
                // 스트림의 각 MemberResult 를 하나씩 꺼내서 MemberResponse 로 변환
                .toList();

    }


    // 내 정보 조회 (회원 상세 조회) (R)
    // /members/me 이런 식으로도 한다고 함!
    @Operation(summary = "내 정보 조회", description = "회원 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/members/{memberId}")
    public MemberDetailResponse getMember(
            @PathVariable Integer memberId
    ) {

        return MemberDetailResponse.from(memberQueryService.getMemberDetail(memberId));
    }


    // 내 정보 수정 (U)
    @Operation(summary = "내 정보 수정", description = "회원 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/members/{memberId}")
    public MemberUpdateResponse updateMember(
            @PathVariable Integer memberId,
            @Valid @RequestBody MemberUpdateRequest request
    ) {

//        request.validate(); 대신 toCommand() 내에서 검증

        MemberUpdateResult result = memberCommandService.updateMember(request.toCommand(memberId));

        return MemberUpdateResponse.from(result);
    }
    /*
        HTTP
         ├─ PathVariable(memberId)  ← 리소스 식별
         └─ Request Body            ← 수정 데이터
                ↓
        Controller 에서 합쳐서
                ↓
        Command (유스케이스 입력 모델)
                ↓
        Service / UseCase
     */


    // ResponseEntity 사용하는 버전은 아래 참고 ▼
    // 내 정보 수정 (U)
/*
    @Operation(summary = "내 정보 수정", description = "회원 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<MemberUpdateResponse> updateMember(
            @PathVariable Integer memberId,
            @RequestBody MemberUpdateRequest request
    ) {
        MemberUpdateResponse response = memberCommandService.updateMember(memberId, request);
        return ResponseEntity.ok(response); // 아래의 축약형
//      └ return ResponseEntity.status(HttpStatus.OK).body(response);
    }
*/

    // 회원 탈퇴 (D)
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.")
    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(
            @PathVariable Integer memberId
    ) {
        memberCommandService.deleteMember(memberId);
    }

}
