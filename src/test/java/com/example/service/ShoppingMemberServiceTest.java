package com.example.service;

import com.example.code.ItemSellStatus;
import com.example.entity.ShoppingItemEntity;
import com.example.entity.ShoppingMemberEntity;
import com.example.entity.ShoppingOrderEntity;
import com.example.entity.ShoppingOrderItemEntity;
import com.example.repository.ShoppingItemRepository;
import com.example.repository.ShoppingMemberRepository;
import com.example.repository.ShoppingOrderItemRepository;
import com.example.repository.ShoppingOrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
//@TestPropertySource(locations = "classpath:application-test.yml") // 어노테이션이 역할을 제대로 못함
@Transactional
public class ShoppingMemberServiceTest {
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

    @Test
    @DisplayName("Auditing 테스트")
    @WithMockUser(username = "gildong", roles = "USER") // 스프링 시큐리티에서 제공하는 어노테이션으로 지정한 사용자가 로그인한 상태라고 가정하고 테스트 진행
    @Rollback(false) // 기본 롤백 동작을 방지합니다.
    public void auditingTest() {
        ShoppingMemberEntity newMember = new ShoppingMemberEntity();
        shoppingMemberRepository.save(newMember);

        em.flush();
        em.clear();

        ShoppingMemberEntity member = shoppingMemberRepository.findById(newMember.getId())
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("register time : " + member.getRegTime());
        System.out.println("update time : " + member.getUpdateTime());
        System.out.println("create member : " + member.getCreatedBy());
        System.out.println("modify member : " + member.getModifiedBy());
    }

    @AfterEach
    void cleanup() {
        System.out.println("테스트 종료 후 정리");
    }
}
