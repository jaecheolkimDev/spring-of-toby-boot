package com.example.repository;

import com.example.code.ItemSellStatus;
import com.example.entity.ShoppingItemEntity;
import com.example.entity.QShoppingItemEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
//@TestPropertySource(locations = "classpath:application-test.yml") // 어노테이션이 역할을 제대로 못함
class ShoppingItemRepositoryTest {
    @Autowired
    ShoppingItemRepository shoppingItemRepository;

    // 영속성 컨텍스트 사용
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        ShoppingItemEntity shoppingItemEntity = new ShoppingItemEntity();
        shoppingItemEntity.setItemNm("테스트 상품");
        shoppingItemEntity.setPrice(10000);
        shoppingItemEntity.setItemDetail("테스트 상품 상세 설명");
        shoppingItemEntity.setItemSellStatus(ItemSellStatus.SELL);
        shoppingItemEntity.setStockNumber(100);
        shoppingItemEntity.setRegTime(LocalDateTime.now());
        shoppingItemEntity.setUpdateTime(LocalDateTime.now());
        ShoppingItemEntity savedShoppingItemEntity = shoppingItemRepository.save(shoppingItemEntity);
        System.out.println(savedShoppingItemEntity.toString());
    }

    public void createItemList() {
        for(int i=1; i<=10; i++) {
            ShoppingItemEntity item = new ShoppingItemEntity();
            item.setItemNm("테스트 상품" +  i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            ShoppingItemEntity savedItem = shoppingItemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        this.createItemList();
        List<ShoppingItemEntity> shoppingItemEntityList = shoppingItemRepository.findByItemNm("테스트 상품1");
        for(ShoppingItemEntity item : shoppingItemEntityList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemDetailTest() {
        this.createItemList();
        List<ShoppingItemEntity> shoppingItemEntityList = shoppingItemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(ShoppingItemEntity item : shoppingItemEntityList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        this.createItemList();
        List<ShoppingItemEntity> shoppingItemEntityList = shoppingItemRepository.findByPriceLessThan(10005);
        for(ShoppingItemEntity item : shoppingItemEntityList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createItemList();
        List<ShoppingItemEntity> shoppingItemEntityList = shoppingItemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(ShoppingItemEntity item : shoppingItemEntityList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTestByQueryAnnotation() {
        this.createItemList();
        List<ShoppingItemEntity> shoppingItemEntityList = shoppingItemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(ShoppingItemEntity item : shoppingItemEntityList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailTestByNativeQuery() {
        this.createItemList();
        List<ShoppingItemEntity> shoppingItemEntityList = shoppingItemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for(ShoppingItemEntity item : shoppingItemEntityList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest() {
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // Querydsl을 통해 쿼리를 생성하기 위한 Q클래스 사용
        QShoppingItemEntity qItem = QShoppingItemEntity.shoppingItemEntity;
        JPAQuery<ShoppingItemEntity> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());

        // 쿼리 결과를 리스트로 반환
        List<ShoppingItemEntity> itemList = query.fetch();

        for(ShoppingItemEntity item : itemList) {
            System.out.println(item.toString());
        }
    }

    public void creatItemList2() {
        for(int i=1; i<=5; i++) {
            ShoppingItemEntity item = new ShoppingItemEntity();
            item.setItemNm("테스트 상품" +  i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            shoppingItemRepository.save(item);
        }

        for(int i=6; i<=10; i++) {
            ShoppingItemEntity item = new ShoppingItemEntity();
            item.setItemNm("테스트 상품" +  i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            shoppingItemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2() {
        this.creatItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QShoppingItemEntity item = QShoppingItemEntity.shoppingItemEntity;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));

        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<ShoppingItemEntity> itemPagingResult = shoppingItemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " + itemPagingResult.getTotalElements());

        List<ShoppingItemEntity> resultItemList = itemPagingResult.getContent();
        for(ShoppingItemEntity resultItem : resultItemList) {
            System.out.println(resultItem.toString());
        }
    }
}