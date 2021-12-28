package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * AllArgsConstrucor VS RequiredArgsConstructor 차이점
 * RequiredArgsConstructor는 final이 선언된 필드만 생성자를 생성해준다.
 * AllArgsConstructor는 모든 필드의 생성자를 생성해준다.
 *
 * 테스트 코드 작성시 필들의 첫 초기화 이후 변경이 되면 안되는 필드들을 final 선언을 해준뒤
 * RequiredArgsConstructor를 사용하는 방법이 더 편리하다.
 */
@Service
//readonly는 select 조회시 성능 향상을 위해 사용한다.
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    //@Autowired
    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */

    @Transactional //변경, 읽기 아닌 쓰게에는 readOnly 사용하면 데이터 변경이 안일어남
    public Long join(Member member){
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 전체 회원 조회
     */

    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
