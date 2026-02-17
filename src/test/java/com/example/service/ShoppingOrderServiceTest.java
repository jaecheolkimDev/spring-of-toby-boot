package com.example.service;

import com.example.code.ItemSellStatus;
import com.example.dto.MemberFormDto;
import com.example.entity.*;
import com.example.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
//@TestPropertySource(locations = "classpath:application-test.yml") // 어노테이션이 역할을 제대로 못함
@AutoConfigureMockMvc   // MockMvc 테스트 설정
@Transactional
public class ShoppingOrderServiceTest {
    /** 테스트 패키지 경로 일치 */
    /** 파일/리소스 테스트: src/test/resources 폴더에 파일을 넣고 테스트합니다. */
    /** 반복 테스트 (@ParameterizedTest): 여러 입력값으로 동일한 테스트를 반복할 때 유용합니다. */

    @Autowired
    private ShoppingOrderRepository shoppingOrderRepository;
    @Autowired
    private ShoppingItemRepository shoppingItemRepository;
    @Autowired
    private ShoppingMemberRepository shoppingMemberRepository;
    @Autowired
    private ShoppingOrderItemRepository shoppingOrderItemRepository;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setup() {
        System.out.println("테스트 시작 전 준비");
    }

    public ShoppingItemEntity createItem() {
        ShoppingItemEntity item = new ShoppingItemEntity();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {
        ShoppingOrderEntity order = new ShoppingOrderEntity();

        for(int i=0; i<3; i++) {
            ShoppingItemEntity item = this.createItem();
            shoppingItemRepository.save(item);
            ShoppingOrderItemEntity orderItem = new ShoppingOrderItemEntity();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setShoppingOrderEntity(order);
        }

        shoppingOrderRepository.saveAndFlush(order);
        em.clear();

        ShoppingOrderEntity savedOrder = shoppingOrderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        Assertions.assertEquals(3, savedOrder.getOrderItemEntityList().size());
    }

    public ShoppingOrderEntity createOrder() {
        ShoppingOrderEntity order = new ShoppingOrderEntity();

        for(int i=0; i<3; i++) {
            ShoppingItemEntity item = createItem();
            shoppingItemRepository.save(item);  // ITEM[INSERT]
            ShoppingOrderItemEntity orderItem = new ShoppingOrderItemEntity();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setShoppingOrderEntity(order);
        }

        ShoppingMemberEntity member = new ShoppingMemberEntity();
        shoppingMemberRepository.save(member);  // MEMBER[SELECT(SEQ) -> UPDATE]

        order.setMember(member);    // FK
        shoppingOrderRepository.save(order);  // ORDER,ORDER_ITEM[SELECT(SEQ) -> UPDATE]
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    @Rollback(false) // 기본 롤백 동작을 방지합니다.
    public void orphanRemovalTest() {
        ShoppingOrderEntity order = this.createOrder();
        order.getOrderItemEntityList().remove(0);
        em.flush(); // MEMBER,ORDER,ORDER_ITEM 3개[INSERT] + ORDER_ITEM 1개[DELETE]
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    @Rollback(false) // 기본 롤백 동작을 방지합니다.
    public void lazyLoadingTest() {
        ShoppingOrderEntity order = this.createOrder(); // 주문 데이터 저장
        Long orderItemId = order.getOrderItemEntityList().get(0).getId();
        em.flush();
        em.clear(); // 영속성 컨텍스트의 상태 초기화

        ShoppingOrderItemEntity orderItem = shoppingOrderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("ShoppingOrderEntity class : " + orderItem.getShoppingOrderEntity().getClass());
    }

    @AfterEach
    void cleanup() {
        System.out.println("테스트 종료 후 정리");
    }
}
