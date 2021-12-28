package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

//Junit에 spring을 사용한다는것을 명시한다.
@RunWith(SpringRunner.class)
//Spring Annotation을 사용한다는것을 명시한다.
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    /**
     * 쿼리 확인 용도 - 데이터 베이스에 저장까지
     */
    //@Rollback(value = false)
    public void 회원가입() throws Exception{
        //give
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);

        /**
         * 쿼리 확인 용도 - 데이터 베이스에 저장은 안함
         */
        em.flush();
        //then
        assertEquals(member, memberRepository.findById(saveId).get());
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception{
        //give
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");
        //when
        memberService.join(member1);
        memberService.join(member2);

        /**
         * 이렇게 설정하는 Test에서 매개변수 옵션을 주면 좀 더 깔끔하게 처리가 가능하다.
         * IllegalStateException 오류가 발생하면 성공적인 테스트라는것을 명시한다.
         */
//        try{
//            memberService.join(member2); //여기서 예외 발생생
//        } catch (IllegalStateException e){
//            return;
//        }
       //then
        fail("예외가 발생해야 한다.");
    }

}
