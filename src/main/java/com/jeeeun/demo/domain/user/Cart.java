package com.jeeeun.demo.domain.user;

import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 아무런 매개변수가 없는 생성자를 생성하되 다른 패키지에 소속된 클래스는 접근을 불허
@Entity
@Table(name = "cart")
public class Cart extends BaseTimeEntity {

    // User (1) ── (1) Cart (1) ── (N) CartItem (N) ── (1) ProductVariant

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK 컬럼 생성, 그 이름이 user_id
    private User user;

    // @Builder.Default
    // @Builder 사용 시 List 기본값 보장, 안 하면 빌더 생성 시 cartItems = null 됨
    // from() 안에서 new Cart() 할 때 = new ArrayList<>() 초기화가 보장되기 때문에
    // 사실상 없어도 되지만 관례상 List 필드에는 붙여두는 게 좋겠다.

    @Builder.Default                                        // 부모 없는 자식 자동 삭제
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public static Cart from(User user) {
        Cart cart = new Cart();
        cart.user = user;
        return cart;
    }


    // ★
    // Cart는 User와 달리 회원가입 시 서버에서 자동으로 생성된다.

    // 사용자 요청이 아닌, 코드가 알아서 만드는 것이기 때문에
    // 타인이 Cart.builder().build() → user 빼먹어도 컴파일 에러가 안 남!

    // 근데 from(user)로 강제하면?
    // Cart.from() → 에러 / Cart.from(user) → OK

    // 추후 코드 누락 방지 겸, Cart 생성 시 user 넣을 것을 강제하는 거다.
    // └ 주인 없는 장바구니는 절대 못 만들게!

}
