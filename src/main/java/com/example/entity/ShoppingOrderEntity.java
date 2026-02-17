package com.example.entity;

import com.example.code.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name="shoppingOrder")
public class ShoppingOrderEntity extends ShoppingBaseEntity {

    @Id
    @Column(name="order_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)   // ORDER엔티티가 MEMBER엔티티를 참조하는 다대일 단방향 매핑
    @JoinColumn(name="member_id")   // 연관관계의 주인을 나타내는 어노테이션
    private ShoppingMemberEntity member;

    private LocalDateTime orderDate;    // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    // 주문상태

    // 외래키가 ORDER_ITEM테이블에 있으므로 연관 관계의 주인은 ORDER_ITEM엔티티이다.
    // mappedBy설정으로 연관 관계의 주인이 아니고 매핑되었음을 알 수 있다.
    // CascadeType.ALL  : 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이
    @OneToMany(mappedBy = "shoppingOrderEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ShoppingOrderItemEntity> orderItemEntityList = new ArrayList<>();
}
