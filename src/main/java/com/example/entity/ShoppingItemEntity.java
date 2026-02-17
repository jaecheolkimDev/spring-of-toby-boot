package com.example.entity;

import com.example.code.ItemSellStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name="shoppingItem")
public class ShoppingItemEntity extends ShoppingBaseEntity {
    @Id
    @Column(name="shopping_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // 상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm;                  // 상품명

    @Column(name="price", nullable = false)
    private int price;                      // 가격

    @Column(nullable = false)
    private int stockNumber;                // 재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;              // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태
}
