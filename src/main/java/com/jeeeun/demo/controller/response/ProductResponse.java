package com.jeeeun.demo.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {
    Integer id;
    String name;
    Double price;
    String description;
    String imageUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
