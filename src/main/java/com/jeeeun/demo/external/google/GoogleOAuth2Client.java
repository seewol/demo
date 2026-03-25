package com.jeeeun.demo.external.google;

// 구글 OAuth2 API와 통신하는 클라이언트

import com.jeeeun.demo.common.config.GoogleAuthProperties;
import com.jeeeun.demo.service.auth.model.GoogleTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

/*  역할 두 가지
 *  1. 인가 코드 → Access Token 교환 (getAccessToken)
 *  2. Access Token → 사용자 정보 조회 (getUserInfo) */

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuth2Client {
    private final GoogleAuthProperties googleAuthProperties;

    // 구글 OAuth2 토큰 발급하는 URL
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    // 구글 사용자 정보 조회 URL
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";


    // [ 1차 요청 ] : 인가 코드 구글에 보내고 AccessToken 받아오기
    public GoogleTokenResponse getAccessToken(String authorizationCode, String redirectUri) {
        // 구글이 요구하는 파라미터들을 form 형태로 구성!
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        // 프론트가 준 인가코드
        params.add("code", authorizationCode);
        // 우리 앱 Client ID
        params.add("client_id", googleAuthProperties.getClientId());
        // 우리 앱 Client Secret
        params.add("client_secret", googleAuthProperties.getClientSecret());
        // 등록된 리디렉션 URI
        params.add("redirect_uri", redirectUri);
        // OAuth2 표준 값 (고정)
        params.add("grant_type", "authorization_code");

        // RestClient 사용해 구글 토큰 API에 POST 요청
        return RestClient.create()
                .post()
                .uri(TOKEN_URL)
                .body(params) // 요청에 데이터 담기
                .retrieve() // 요청 보내고 응답 받아오기
                .body(GoogleTokenResponse.class); // 응답 내용 해당 객체로 변환
        // 구글이 JSON 형태로 응답을 보내면 그걸 GoogleTokenResponse 레코드에 자동 매핑

    }

    // [ 2차 요청 ] : Access Token 사용해 구글 사용자 정보 조회
    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserInfo(String accessToken) {
        // 구글이 준 사용자 정보가 JSON인데 이걸 Map으로 받음
        // key : String, value : 여러 타입이라 Object

        return RestClient.create()
                .get()
                .uri(USER_INFO_URL)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class); // 응답 JSON → Map으로 변환
    }

}
