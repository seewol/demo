package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.MemberCreateRequest;
import com.jeeeun.demo.controller.request.MemberUpdateRequest;
import com.jeeeun.demo.controller.response.MemberCreateResponse;
import com.jeeeun.demo.controller.response.MemberResponse;
import com.jeeeun.demo.controller.response.MemberUpdateResponse;
import com.jeeeun.demo.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor // final 붙은 애들만 생성자 만들어 줌.
@RestController
// API 엔드 포인트 담당
public class MemberController {

    private final MemberService memberService;

    /*
        -- 전체 흐름
        클라이언트 → [Controller] → [Service] → [Repository] → DB(member 테이블)
                         ↑                                      ↓
                      [JSON]  ←  [MemberResponse DTO]  ←  [Member Entity]
     */


    // 회원 가입 (C)
    @PostMapping("/members")
    public ResponseEntity<MemberCreateResponse> createMember(
            @Valid @RequestBody MemberCreateRequest request
    ) {
        MemberCreateResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    // 멤버 목록 조회 (R)
    @GetMapping("/members")
    public List<MemberResponse> getMembers(){

//        return List.of();
        return memberService.getMembers();
    }

    // 내 정보 수정 (U)
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<MemberUpdateResponse> updateMember(
            @PathVariable Integer memberId,
            @RequestBody MemberUpdateRequest request
    ) {
        MemberUpdateResponse response = memberService.updateMember(memberId, request);
        return ResponseEntity.ok(response); // 아래의 축약형
//      └ return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 내 정보 조회
    @GetMapping("/member/{memberId}")
    public MemberResponse getMember(
            @PathVariable("memberId") Integer memberId
    ) {
        return new MemberResponse(
                1,
                "jeeneepark@naver.com",
                "박지은",
                "01038561226",
                LocalDateTime.parse("2025-08-01T12:34:56"),
                LocalDateTime.parse("2025-09-02T10:00:00")
        );
    }
    
}
