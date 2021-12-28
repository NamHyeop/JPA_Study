package study.datajpa.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    /**
     * .yml의 글로벌 설정말고 여기서 5개씩 page를 보고싶다면 아래와 같이 설정 가능하다.
     */
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
        /**
         * API 사용할 때 반환할때는 항상 DTO로
         * 아래의 코드들의 차이를 기억하자. 생성자를 만들면 확실히 코드가 좀 더 깔끔해진다.
         */
        Page<Member> page = memberRepository.findAll(pageable);
//        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        Page<MemberDto> map = memberRepository.findAll(pageable).map(MemberDto::new);
        return map;
    }

//    @PostConstruct
    public void init(){
        for (int i = 0; i < 100; i++){
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
