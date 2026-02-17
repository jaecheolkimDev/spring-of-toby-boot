package com.example.entity;

import com.example.code.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name="shoppingOrderItem")
public class ShoppingOrderItemEntity extends ShoppingBaseEntity {

    @Id
    @Column(name="order_item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)   // ORDER_ITEM엔티티가 ITEM엔티티를 참조하는 다대일 단방향 매핑
    @JoinColumn(name="item_id")   // 연관관계의 주인을 나타내는 어노테이션
    private ShoppingItemEntity item;

    @ManyToOne(fetch = FetchType.LAZY)  // ORDER_ITEM엔티티가 ORDER엔티티를 참조하는 다대일 단방향 매핑
    @JoinColumn(name = "order_id")   // 연관관계의 주인을 나타내는 어노테이션
    private ShoppingOrderEntity shoppingOrderEntity;

    private int orderPrice;     // 주문가격

    private int count;          // 수량

    // 롬복의 @Setter가 생성하는 setOrder 대신 이 메서드가 실행됨
    // 롬복은 컴파일 시점에 메서드를 생성합니다. 하지만, 이미 같은 이름과 파라미터를 가진 메서드가 존재하면, 생성하지 않음.
    public void setShoppingOrderEntity(ShoppingOrderEntity shoppingOrderEntity) {
        this.shoppingOrderEntity = shoppingOrderEntity;
        // 주인이 아닌 쪽(shoppingOrderEntity)의 리스트에도 나 자신을 추가하는 로직 포함(객체 상태의 일관성 유지)
        // 양방향일때 관계를 이미 맺었을때 방어하는 로직
        if (shoppingOrderEntity != null) {
            // DB저장에는 영향을 주지 않지만 객체 지향적인 안전성 때문에 작성함(1차 캐시, 테스트)
            shoppingOrderEntity.getOrderItemEntityList().add(this);
        }
    }

    // 양방향일때는 @ToStrig.Exclude 제외해야함. 서로 호출하다가 StackOverflowError 발생함
}
