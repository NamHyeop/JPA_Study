package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//readOnly = true 옵션을 주면 더티체킹과 같은 불필요한 리소스를 줄여서 더 빨리 검색함. 조회용에만 이렇게 사용해야한다.
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    /**
     * 아래와 같은 필드식 보다는 생성자로 주입하는게 테스트코드나 이후 관리에서 더 편리함
     */
//    @Autowired
//    private MemberRepository memberRepository;

    /**
     * Rombok의 RequiredArgsConstructor를 사용하면 final에 자동 주입해준다. 이게 최고의 방법
     */
    private final MemberRepository memberRepository;

    //Autowired 한 개 일때는 생략이 가능하다.
//    @Autowired
//    public MemberService(MemberRepository memberRepository){
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원가입
     */

    //이건 조회용이 아니기 때문에 따로 명시함
   @Transactional
    public Long join(Member member){
        //회원 중복 검사 로직
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member){
        /**
         * 실무에서는 여러 서버에서 멀티스레드로 동작해 동시간대에 똑같은 이름으로 접근하게 되는 경우가 있음
         * 그렇기 때문에 최후의 보루로 member.getname에 unique 조건을 걸어주는것이 좋다.
         */
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //기본키값으로 단일 회원을 찾는 방법법
   public Member findOne(Long memberId){
        return memberRepository.findById(memberId).get();
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id).get();
        member.setName(name);
    }
}
