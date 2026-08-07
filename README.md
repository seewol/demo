# 🪬 Kama
**Kama** ─ 작은 욕망을 담은 이커머스 프로젝트입니다.


처음엔 단순하게 시작했는데, 만들다 보니 질문이 계속 생기더라고요!
<br/>

_"결제는 됐는데 재고가 없으면?"_<br/>
_"비로그인으로 담은 장바구니는?"_<br/>
_"상품 정보가 바뀌면 주문 내역은?".._<br/>

평소 쇼핑을 좋아하다 보니 사용자 입장에서 신경 쓰이는 것들이 많았어요.<br/>
나만의 작은 욕망들을 하나씩 해결해 간 기록이자 프로젝트랍니다!

<br/>


## 💼 Tech Stack

<br/>

| 구분 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.8 |
| ORM | Spring Data JPA, Querydsl |
| Build | Gradle |
| Database | MySQL |
| Auth | Spring Security, JWT |
| External API | PortOne V1 (결제), Google OAuth2 |
| Infra (예정) | Docker, GitHub Actions | 

<br/>



## 📂 Package Structure

<br/>

```
src/main/java/com/jeeeun/demo
├── common
│   ├── config          # Security, Swagger, Scheduler 등
│   ├── error            # ErrorCode, GlobalExceptionHandler
│   └── jpa               # BaseTimeEntity
├── config
│   └── data              # 초기 더미 데이터 생성 (DataInitializer)
├── controller             # Auth/User/Product/Variant/Cart/Order/Shipping 컨트롤러
│   ├── request            # 요청 DTO
│   └── response           # 응답 DTO
├── domain
│   ├── order               # 주문/주문아이템 엔티티
│   ├── product              # 상품/조합/재고 관련 엔티티
│   ├── shipping              # 배송 엔티티
│   └── user                   # 회원/장바구니 엔티티
├── external
│   ├── google                 # Google OAuth2 클라이언트
│   └── portone                 # PortOne 결제 클라이언트
├── repository
│   ├── order
│   ├── product
│   ├── shipping
│   └── user
├── service                     # Auth/User/Product/Cart/Order/Shipping 서비스
│   ├── auth/model
│   ├── cart/model
│   ├── order/model              # Command/Result 패턴
│   ├── product/model
│   ├── shipping/model
│   └── user/model
└── util                          # JWT Provider/Filter
    └── exception                 # 커스텀 인증 예외
```

<br/>



## 🛠️ 구현 기능

<br/>

### 👤 Auth
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /auth/sign-up | 회원가입 | ❌ |
| POST | /auth/sign-in | 로컬 로그인 | ❌ |
| POST | /auth/sign-in/google | 구글 소셜 로그인 | ❌ |
| POST | /auth/refresh | 토큰 재발급 | ❌ |

### 👤 User
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | /users/me | 내 정보 조회 | ✅ |
| PATCH | /users/me | 내 정보 수정 | ✅ |
| DELETE | /users/me | 회원 탈퇴 (soft delete) | ✅ |

### 🛍️ Product
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /products | 상품 등록 | ✅ ADMIN |
| GET | /products | 상품 목록 조회 (페이징/검색/필터) | ❌ |
| GET | /products/{productId} | 상품 상세 조회 | ❌ |
| PATCH | /products/{productId} | 상품 수정 (부분 수정) | ✅ ADMIN |
| DELETE | /products/{productId} | 상품 삭제 (soft delete) | ✅ ADMIN |
| POST | /products/{productId}/variants | 상품 조합 등록 | ✅ ADMIN |
| PATCH | /variants/{variantId}/stock | 재고 수정 | ✅ ADMIN |

### 🛒 Cart
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /cart/items | 장바구니 아이템 추가 | ✅ |
| GET | /cart | 내 장바구니 조회 | ✅ |
| PATCH | /cart/items/{cartItemId} | 수량 변경 | ✅ |
| DELETE | /cart/items/{cartItemId} | 아이템 삭제 | ✅ |
| POST | /cart/merge | 비로그인 장바구니 병합 | ✅ |

### 📦 Order
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /orders | 주문 생성 (장바구니) | ✅ |
| POST | /orders/direct | 바로구매 | ✅ |
| GET | /orders | 주문 목록 조회 | ✅ |
| GET | /orders/{orderId} | 주문 상세 조회 | ✅ |
| PATCH | /orders/{orderId}/cancel | 주문 취소 + 재고 복구 | ✅ |
| PATCH | /orders/{orderId}/items/{orderItemId}/cancel | 아이템 단위 부분 취소 (수량 지정) + 재고 복구 + 부분환불 | ✅ |

### 🚚 Shipping
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| PATCH | /shippings/{shippingId}/ship | 배송 시작 처리 | ✅ ADMIN |
| PATCH | /shippings/{shippingId}/delay | 발송 지연 처리 | ✅ ADMIN |
| PATCH | /shippings/{shippingId}/deliver | 배송 완료 처리 | ✅ ADMIN |
<br/>



## 💡 설계 고민 방향

<br/>

### 재고 선점
결제가 완료됐는데 재고가 없어서 취소되는 상황은 없어야 하잖아요!  
그래서 **검증 루프와 차감 루프를 분리**했습니다 — 모든 상품의 재고가 충분하다고 확인된 후에만 차감합니다.

```java
// 1단계: 전체 검증 (차감 없이)
for (CartItem cartItem : cartItems) {
    if (stock.getQuantity() < cartItem.getQuantity()) {
        throw new BusinessException(ErrorCode.OUT_OF_STOCK);
    }
    stocks.add(stock);
}

// 2단계: 전부 통과하면 그때 차감
for (int i = 0; i < cartItems.size(); i++) {
    stocks.get(i).decrease(cartItems.get(i).getQuantity());
}
```

<br/>

### 결제 보상 로직
PortOne 외부 결제는 `@Transactional` 롤백 범위 밖이에요.  
DB는 롤백되는데 실제 결제가 그대로 남는 상황을 방지하기 위해,  
비즈니스 로직 실패 시 catch 블록에서 환불을 직접 요청하도록 했어요.

```java
try {
    // 주문 생성 로직
} catch (BusinessException e) {
    portOneClient.cancelPayment(command.impUid()); // 포트원에 환불 요청
    throw e;                                        // DB 롤백 유도
}
```

<br/>

### 비로그인 장바구니 병합
비로그인 상태에서 localStorage에 담아둔 상품을, 로그인 후 서버 장바구니와 합치도록 했어요.  
이미 삭제됐거나 품절인 상품은 에러 없이 조용히 넘기고, localStorage 초기화는 프론트가 담당해요.

<br/>

### 도메인 메서드
`setter` 대신, 상태 변경에 규칙이 따라오는 경우엔 `엔티티`가 직접 책임집니다.

```java
// 삭제된 상품은 수정 불가 — Product 스스로 판단
public void delete() {
    if (this.isDeleted) throw new BusinessException(ErrorCode.ALREADY_DELETED_PRODUCT);
    this.isDeleted = true;
}

// 취소 가능한 상태인지 — Order 스스로 판단
public void cancel() {
    if (this.status != PENDING && this.status != PAID)
        throw new BusinessException(ErrorCode.CANNOT_CANCEL_ORDER);
    this.status = CANCELLED;
}
```

<br/>

### 주문 스냅샷
주문 생성 시점에 상품명, 조합명, 썸네일, 단가를 `OrderItem`에 저장합니다.  
이후 상품 정보가 바뀌거나 삭제되더라도, 주문 내역은 항상 당시 기준으로 보입니다.

<br/>

### 할인 스케줄러
할인 상태를 조회할 때마다 계산하는 대신, 스케줄러가 DB를 직접 업데이트합니다.

| 스케줄러 | 실행 시각 | 역할 |
|---|---|---|
| 할인 만료 / 시작 | 매일 자정 | `bulkExpireDiscounts` + `bulkStartDiscounts` |
| 장바구니 만료 | 매일 새벽 2시 | updatedAt 기준 30일 지난 CartItem 삭제 |

<br/>



## 🔥 트러블슈팅

### PortOne V1 테스트 결제 404
테스트 채널로 결제 후 결제 정보를 조회하니 404가 떴어요!  
알고 보니 2026년 1월 26일부터 PortOne 보안 정책이 바뀌어서,  
테스트 채널 결제 조회 시 `?include_sandbox=true` 파라미터가 필수였습니다.  
URL 뒤에 파라미터를 붙여 해결했지만, 외부 API는 정책 변경을 놓치면 바로 장애로 이어진다는 걸 배웠습니다.

> 참고: https://help.portone.io/news/content/notice-v1-api-2025-11-25

<br/>

### MySQL ENUM 컬럼, 새 값 추가해도 DDL에 자동 반영 안 됨
아이템 부분취소에 `PARTIALLY_CANCELLED` 상태를 추가했는데, 저장 시점에
`Data truncated for column 'order_item_status'` 에러가 났어요.  
알고 보니 `@Enumerated(EnumType.STRING)`을 써도, `ddl-auto: update` 모드에서는
이미 존재하는 컬럼의 MySQL 네이티브 ENUM 제약조건까지는 자동으로 안 넓혀지더라고요.  
컴파일은 멀쩡하고 런타임에만 터지는 케이스라 처음엔 당황했는데,
`ALTER TABLE ... MODIFY COLUMN`으로 허용값을 직접 늘려서 해결했습니다.

<br/>

### 부분취소 + 전체취소 조합 시 재고 중복 복구
아이템을 개별로 부분취소한 뒤 남은 아이템까지 전체취소하면, 전체취소 로직이
이미 복구된 수량까지 또 복구해버리는 버그가 있었어요.  
`quantity - cancelledQuantity`로 실제 남은 수량만 계산해서 복구하도록 고쳤습니다.

<br/>

### `/sign-up` 경로, 문서와 실제 라우팅 불일치
README와 SecurityConfig 둘 다 `/sign-up`으로 문서화돼 있었는데,
`AuthController`가 클래스 레벨에 `@RequestMapping("/auth")`를 갖고 있어서
실제 경로는 `/auth/sign-up`이었어요. 테스트 중 401(권한 없음) → 404(경로 없음)로
이어지는 과정에서 발견해서, 문서를 실제 라우팅 기준으로 정정했습니다.

<br/>



## 🗂️ ERD

> 추후 이미지로 교체 예정

```
User
├── UserCredentials (로컬 / 구글 인증)
├── Cart
│   └── CartItem ─────────────────── ProductVariant
└── Order
    ├── OrderItem (스냅샷 저장) ───── ProductVariant
    └── Shipping

Product
├── Category
├── ProductImage
├── ProductOption
│   └── ProductOptionDetail
└── ProductVariant
    └── ProductStock
```

<br/>



## ⬜ 진행 예정

**리팩토링**
- [x] 배송비 + 배송 도메인
- [x] 아이템 단위 부분 취소 (라인 단위 → 수량 단위로 확장 완료)
- [ ] 주문상세에 아이템별 취소상태 노출 (OrderItemResult/OrderDetailItemResult에 orderItemId·status·cancelledQuantity 추가, cancelOrder()도 아이템별 상태 반영하도록 확장)
- [ ] 장바구니 + 주문상세 additionalPrice 분리 표시
- [ ] cancelOrder reason 파라미터화
- [ ] 무통장입금 (가상계좌) + Webhook
- [ ] 상품 이미지 수정 API
- [ ] 상품 옵션/조합 수정 API

**4단계**
- [ ] Swagger 정리
- [ ] AOP 로깅
- [ ] Docker + CI/CD (GitHub Actions)