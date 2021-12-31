package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

//RestController = ResoposeBody + controller
@RestController
@RequiredArgsConstructor
public class MemberApiController { 
    
    private final MemberService memberService;

    /**
     * 조회예제1
     * 엔터티를 그대로 넘겨주어서 다양한 문제들이 발생함
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("/test")
    public String test1(){
        return "OK";
    }

    /**
     * DTO를 만듬으로써 V1의 문제들을 보완
     */
    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream()
                .map(m -> new MemberDTO(m.getName()))
                .collect(Collectors.toList());
        /**
         * 어떤 자료형이 들어올지 모르기 때문에 Result DTO를 한 번 씌여서 반환해준다.
         * 그래야 JSON에 배열로 깨지지 않고 여러 데이터를 추가할 수 있다.
         * 이해 안가면 실행 데이터 확인해보자
         */
        return new Result(collect);
    }

    /**
     * DTO들
     */
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO{
        private String name;
    }

//===================================위에는 조회예제, 밑에는 수정 예제===================
    /**
     * 가입예제1
     * V1 처럼 엔터티를 Member 그자체를 사용할 경우 API 스펙이 바뀌면 큰일이 나버린다.(like member.name을 member.username으로 바꾼다면?)
     * 그러므 DTO를 따로 만들어서 사용해야한다. -> V2에서 보완해보자
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 가입예제2
     * v2에서는 CreateMemberRequest DTO를 만들었다.
     * 이런 경우 API 스펙이 바뀌었을 경우 이 부분만 바꾸면 되므로 지장이 없다.
     * 이런 DTO를 만드는것이 API의 기본형식이다.
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 수정 예제
     * 수정을 할 때에는 응답 DTO랑 수신 DTO를 따로 만들어주는것이 좋다.
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        /**
         * update를 하면서 안에서 cmd를 작성해도 되지만 cmd와 query를 분리하고자 여기에 작성함
         */
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
