package com.example.entity;

import com.example.code.Role;
import com.example.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Setter
@ToString
@Table(name="shoppingCart")
public class ShoppingCartEntity extends ShoppingBaseEntity {

    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne   // CART엔티티가 MEMBER엔티티를 참조하는 단방향 매핑
    @JoinColumn(name="member_id")
    private ShoppingMemberEntity member;
}
