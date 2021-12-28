package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        /**
         * Spring Data JPA가 프록시를 활용하고 구현체를 넣어주는것을 확인할 수 있다.
         */
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember =
                memberRepository.findById(savedMember.getId()).get();
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성보장
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        /**
         * 단건 조회 검증
         */
        assertThat(member1.getId()).isEqualTo(findMember1.getId());
        assertThat(member1.getUsername()).isEqualTo(findMember1.getUsername());
        assertThat(member1).isEqualTo(findMember1);

        assertThat(member2.getId()).isEqualTo(findMember2.getId());
        assertThat(member2.getUsername()).isEqualTo(findMember2.getUsername());
        assertThat(member2).isEqualTo(findMember2);

        /**
         * 리스트 조회 검증
         */
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        /**
         * 카운트 검증
         */
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        /**
         * 삭제 검증
         */
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernamesAndAgeGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("BBB");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);


        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result1 = memberRepository.findListByUsername("AAA");
        Member result2 = memberRepository.findMemberByUsername("BBB");
        Optional<Member> result3 = memberRepository.findOptionalByUsername("AAA");

        System.out.println("result1 = " + result1);
        System.out.println("result2 = " + result2);
        System.out.println("result3 = " + result3);

        /**
         * 만약 존재하지 않는 값을 조회할 경우 리스트는 0을 반환하고 단일 조회는 NULL을 반환한다.
         */
        List<Member> notExistresult1 = memberRepository.findListByUsername("CCC");
        Member NotExistresult2 = memberRepository.findMemberByUsername("CCC");
        Optional<Member> notExistResult2 = memberRepository.findOptionalByUsername("CCC");

        System.out.println("notExistresult1 = " + notExistresult1);
        System.out.println("NotExistresult2 = " + NotExistresult2);
        System.out.println("notExistResult2 = " + notExistResult2);
    }

    @Test
    public void paging() throws Exception{
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        /**
         * sort 정보는 생략 가능
         */
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        /**
         * 페이지를 위에 처럼 Member 그자체를 반환하면 절대 안된다. 반드시 DTO를 사용해서 반환해야한다.
         * 아래처럼 map을 사용하면 편리하다.
         */
        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        /**
         * 페이징한 정보를 조회할때는 .getContent를 사용
         */
        //when
        List<Member> content = page.getContent();
        /**
         * 페이징한 정보의 개수
         */
        long totalElements = page.getTotalElements();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        /**
         * 페이지 번호 정보 조회 3개씩 가져왔으니 0번째 페이지이다.
         */
        assertThat(page.getNumber()).isEqualTo(0);
        /**
         * 총 페이지수 조회 5개의 페이지를 3개씩 보니 2개가 나옴
         */
        assertThat(page.getTotalPages()).isEqualTo(2);
        /**
         * 현재 페이지가 첫번째 페이지인가
         */
        assertThat(page.isFirst()).isTrue();
        /**
         * 현재 페이지의 다음페이지가 있는가
         */
        assertThat(page.hasNext()).isTrue();
    }
    /**
     * sllicing 예제
     */
//    @Test
//    public void slicing() throws Exception{
//        //given
//        memberRepository.save(new Member("member1", 10));
//        memberRepository.save(new Member("member2", 10));
//        memberRepository.save(new Member("member3", 10));
//        memberRepository.save(new Member("member4", 10));
//        memberRepository.save(new Member("member5", 10));
//
//        int age = 10;
//        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
//
//        List<Member> content = page.getContent();
//
//         assertThat(content.size()).isEqualTo(3);
////        assertThat(page.getTotalElements()).isEqualTo(5);
//        assertThat(page.getNumber()).isEqualTo(0);
////        assertThat(page.getTotalPages()).isEqu alTo(2);
//        assertThat(page.isFirst()).isTrue();
//        assertThat(page.hasNext()).isTrue();
//    }

    @Test
    public void bulkUpdate() throws Exception{
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCnt = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();
        List<Member> result = memberRepository.findByUsername("member5");

        /**
         * 기대값이 41이라면 어림도 없지.. 영속화 영역에 있기 때문에 40이 나온다.
         * em.flush(), em.clear() 넣으면 해결 가능
         */
        Member member = result.get(0);
        System.out.println("member = " + member);
        assertThat(resultCnt).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception{
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        /**
         * Lazy 설정으로 인해 feth join을 사용안하면 n + 1 문제 발생한다.
         * 아니면 EntityGraph를 사용해야한다.
         */
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
       // List<Member> members = memberRepository.findMemberFetchJoin();

        /**
         * N + 1 문제 때문에 패치조인 사용해야한다.
         */
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getClass());
            System.out.println("member = " + member.getTeam().getName());
            //member.getTeam().getName();
        }
    }

    @Test
    public void queryHint(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        /**
         * 읽기 전용으로 최적화 할 경우 변경을 시도해도 변경이 안된다.
         * 읽기 전용으로 최적화 하는 경우에 사용하자
         */
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
        em.clear();

        Member member = memberRepository.findById(member1.getId()).get();
        System.out.println("member = " + member.getUsername());
    }

    @Test
    public void lock(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }
}
