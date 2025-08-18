package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.controller.response.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProductRequest {
    String name;
    Double price;
    String description;
    String imageUrl;

    // 상품 수정
    public void applyTo(ProductResponse updated) {
        updated.setName(this.name);
        updated.setPrice(this.price);
        updated.setDescription(this.description);
        updated.setImageUrl(this.imageUrl);
        updated.setUpdatedAt(LocalDateTime.now());

        // 서버에서 찾은 updated(ProductResponse) 를 수정하는 것.
    }
}
