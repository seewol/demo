package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.MemberCreateRequest;
import com.jeeeun.demo.controller.request.MemberUpdateRequest;
import com.jeeeun.demo.controller.response.*;
import com.jeeeun.demo.service.MemberCommandService;
import com.jeeeun.demo.service.MemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor // final 붙은 애들만 생성자 만들어 줌.
@RestController
@Tag(name = "MemberController", description = "Member CRUD API 엔드포인트")
// API 엔드 포인트 담당
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    /*
        -- 전체 흐름
        클라이언트 → [Controller] → [Service] → [Repository] → DB(member 테이블)
                         ↑                                      ↓
                      [JSON]  ←  [MemberResponse DTO]  ←  [Member Entity]
     */

    // 회원 가입 (C)
    @Operation(summary = "회원 가입", description = "회원을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "회원 등록 성공")
    @PostMapping("/members")
    public MemberCreateResponse createMember(
            @Valid @RequestBody MemberCreateRequest request
    ) {
        return memberCommandService.createMember(request);
    }

    // 멤버 목록 조회 (R)
    @Operation(summary = "회원 목록 조회", description = "회원 전체 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/members")
    public List<MemberResponse> getMembers(){

//        return List.of();
        return memberQueryService.getMembers();
//            List<MemberResponse> members = memberQueryService.getMembers();
//            return members;
    }

    // 내 정보 조회 (회원 상세 조회)
    @Operation(summary = "내 정보 조회", description = "회원 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/members/{memberId}")
    public MemberDetailResponse getMember(
            @PathVariable Integer memberId
    ) {
        return memberQueryService.getMemberDetail(memberId);
    }

    // 내 정보 수정 (U)
    @Operation(summary = "내 정보 수정", description = "회원 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/members/{memberId}")
    public MemberUpdateResponse updateMember(
            @PathVariable Integer memberId,
            @RequestBody MemberUpdateRequest request
    ) {
        return memberCommandService.updateMember(memberId, request);
    }

    // 회원 탈퇴 (D)
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    @DeleteMapping("/members/{memberId}")
    public MemberDeleteResponse deleteMember(
            @PathVariable Integer memberId
    ) {
        return memberCommandService.deleteMember(memberId);
    }

    // ResponseEntity 사용하는 버전은 아래 참고!
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
}
