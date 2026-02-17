package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name="shoppingCartItem")
public class ShoppingCartItemEntity extends ShoppingBaseEntity {

    @Id
    @Column(name="cart_item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_item_id")
    private ShoppingCartEntity cart;

    @ManyToOne
    @JoinColumn(name = "shopping_item_id")
    private ShoppingItemEntity item;

    private int count;
}
