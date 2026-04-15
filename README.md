# My Demo Project

Spring Boot + Gradle 학습용 프로젝트! 🎉  
이커머스 관련 기본 API를 구현하며 스프링 부트와 JPA를 연습합니다.


---


## 🚀 Tech Stack
- Java 17
- Spring Boot 3.4.8
- Spring Data JPA
- Gradle


---


## 📂 Project Structure
```
src
├─ main
│  ├─ java/com/jeeeun/demo
│  │  ├─ common
│  │  │  ├─ config          # 앱 설정 (JPA, Swagger 등)
│  │  │  ├─ error           # 공통 예외처리 (ErrorCode, GlobalExceptionHandler 등)
│  │  │  └─ jpa             # 공통 JPA 설정 (BaseTimeEntity 등)
│  │  ├─ controller
│  │  │  ├─ request         # 요청 DTO
│  │  │  └─ response        # 응답 DTO
│  │  ├─ domain
│  │  │  ├─ product         # 상품 엔티티
│  │  │  └─ user            # 회원 엔티티
│  │  ├─ external
│  │  │  └─ google          # Google OAuth2 클라이언트
│  │  ├─ repository         # JPA Repository
│  │  ├─ service
│  │  │  ├─ product/model   # 상품 Command/Result 모델
│  │  │  └─ user/model      # 회원 Command/Result 모델
│  │  └─ util               # JWT 등 유틸리티
│  └─ resources
│     ├─ application.yml         # 공통 설정
│     └─ application-local.yml   # 로컬 환경 설정 (gitignore)
└─ test                          # 테스트 코드
```

---


## ✅ 구현 기능

### 👤 Auth
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /sign-up | 회원가입 | ❌ |
| POST | /auth/sign-in | 로컬 로그인 | ❌ |
| POST | /auth/sign-in/google | 구글 소셜 로그인 | ❌ |
| POST | /auth/refresh | 토큰 재발급 | ❌ |

### 👤 User
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | /users/me | 내 정보 조회 | ✅ |
| PATCH | /users/me | 내 정보 수정 | ✅ |
| DELETE | /users/me | 회원 탈퇴 | ✅ |

### 🛍️ Product
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /products | 상품 등록 | ✅ ADMIN |
| GET | /products | 상품 목록 조회 (페이징, 검색, 필터) | ❌ |
| GET | /products/{productId} | 상품 상세 조회 | ❌ |
| POST | /products/{productId}/variants | 상품 조합 등록 | ✅ ADMIN |
| PATCH | /variants/{variantId}/stock | 재고 수정 | ✅ ADMIN |


---
