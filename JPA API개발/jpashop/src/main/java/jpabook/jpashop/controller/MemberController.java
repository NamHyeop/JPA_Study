package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원 등록 영역
     */

    @GetMapping("/members/new")
    public String createFrom(Model model){
        //Get인데도 굳이 빈 껍데기 MemberForm을 넘기는것은 Validaton 검증이나 여러 혜택을 위해서다.
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){
        /**
         * 오류 발생시 BindingResult를 활용해서 다시 회원가입창을 넘겨준다.
         */
        if(result.hasErrors()){
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        /**
         * 회원 데이터 저장
         */
        memberService.join(member);
        /**
         * home으로 다시 redirect한다
         */
        return "redirect:/";
    }

    /**
     * 회원 목록 조회 영역
     */

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        /**
         * 인라인 하면 좀 더 깔끔한 코드가가능함(cmd + option + n{ctrl + alt + t})
         */
//        model.addAttribute("members", memberService.findMembers());
        return "members/memberList";
    }

}
