package com.example.repository;

import com.example.entity.ShoppingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoppingItemRepository extends JpaRepository<ShoppingItemEntity, Long>, QuerydslPredicateExecutor<ShoppingItemEntity> {
    List<ShoppingItemEntity> findByItemNm(String itemNm);

    List<ShoppingItemEntity> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    List<ShoppingItemEntity> findByPriceLessThan(Integer price);

    List<ShoppingItemEntity> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from ShoppingItemEntity i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<ShoppingItemEntity> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<ShoppingItemEntity> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}
