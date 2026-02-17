package com.example.service;

import com.example.dto.MemberFormDto;
import com.example.entity.ShoppingCartEntity;
import com.example.entity.ShoppingMemberEntity;
import com.example.repository.ShoppingCartRepository;
import com.example.repository.ShoppingMemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
//@TestPropertySource(locations = "classpath:application-test.yml") // 어노테이션이 역할을 제대로 못함
@AutoConfigureMockMvc   // MockMvc 테스트 설정
@Transactional
public class ShoppingCartServiceTest {
    /** 테스트 패키지 경로 일치 */
    /** 파일/리소스 테스트: src/test/resources 폴더에 파일을 넣고 테스트합니다. */
    /** 반복 테스트 (@ParameterizedTest): 여러 입력값으로 동일한 테스트를 반복할 때 유용합니다. */

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ShoppingMemberRepository shoppingMemberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setup() {
        System.out.println("테스트 시작 전 준비");
    }

    @Test
    @DisplayName("createMember 테스트")
    public ShoppingMemberEntity createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return ShoppingMemberEntity.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest() {
        ShoppingMemberEntity member = createMember();
        shoppingMemberRepository.save(member);


        ShoppingCartEntity cart = new ShoppingCartEntity();
        cart.setMember(member);
        shoppingCartRepository.save(cart);

        // JPA는 영속성 컨텍스트에 데이터를 저장 후 트랜잭션이 끝날 때 flush()를 호출하여 데이터베이스에 반영합니다.
        // JPA는 영속성 컨텍스트로부터 엔티티를 조회 후 없을 경우 데이터베이스를 조회합니다.
        em.flush();     // 데이터베이스에 반영합니다.(쓰기 지연이라 반영해줘야함)
        // 실제 데이터베이스에서 CART엔티티를 가지고 올 때 MEMBER엔티티도 같이 가지고오는지 보기 위해서 영속성 컨텍스트를 비워줌
        em.clear();

        // 매핑된 엔티티도 한 번에 조회하는것을 즉시로딩 이라고 함
        // 일대일/다대일로 매핑할 경우 즉시 로딩을 기본 Fetch 전략으로 설정합니다.
        // 별도 설정이 없으면 FetchType.EAGER(즉시로딩)로 설정하는 것과 동일합니다. ex) @OneToOne(fetch = FetchType.EAGER)
        ShoppingCartEntity savedCart = shoppingCartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(savedCart.getMember().getId(), member.getId());
    }

    @AfterEach
    void cleanup() {
        System.out.println("테스트 종료 후 정리");
    }
}
