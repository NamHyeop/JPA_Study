package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.*;


@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("MEMBER1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    /**
     * JPQL로 작성한 DB 조회 방법(QueryDsl과의 차이점에 주목. JPQL은 컴파일 시점전에 오류를 못잡아준다.)
     */
    @Test
    public void startJPQL() {
        //member1을 찾아라
        String qlString = "select m from Member m " +
                "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * QueryDSL로 작성한 DB 조회 방법(컴파일 직전 오류 발견 가능. 좀더 편리)
     */
    @Test
    public void startQuerydsl1() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember mem = new QMember("m");
//        QMember member = QMember.member;
        Member findMember = queryFactory
                .select(mem)
                .from(mem)
                .where(mem.username.eq("member1"))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * QueryDSL로 작성한 DB 조회 방법(JpaQueryFactory를 전역으로 빼서 간결화 한 방법)
     */
    @Test
    public void startQuerydsl2() {
        QMember m = new QMember("m");
        Member member1 = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();
        assertThat(member1.getUsername()).isEqualTo("member1");
    }

    /**
     * --가장 권장하는 방법--
     * QueryDSL의 기본 Q-Type을 활용한 DB 조회 방법(staic import로 좀 더 직관적이고 간결하다)
     * 주의
     * 같은 테이블을 조인해야할 때는 별칭을 사용해서 조인해야만 함. 아니면 방법 없다.
     */
    @Test
    public void startQueryDsl3() {
        /**
         * 복습할 때 햇갈릴수 있으니까 별칭과, 변수명 구분되는거 확인해보자
         */
//        QMember m2 = new QMember("m1");
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * QueryDsl에 검색 조건 걸어서 DB 조회하기
     */
    @Test
    public void search() {
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * QueryDsl에 검색 조건중 ANd 걸어서 DB 조회하기(위와 동일한데 and를 ,로 구분했다)
     */
    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * QueryDsl의 결과 조회 부분
     */
    @Test
    public void resultFetch() {
        /**
         * fetch()를 사용한 리스토 조회, 데이터가 없으면 빈 리스트를 반환
         */
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        /**
         * fetchOne()을 사용한 단 건 조회, 결과가 없으면 null 반환, 두 건 이상일 경우 com.querydsl.core.NonUniqueResultException 오류 발생
         * 현재 예제 기준 member가 4명 이므로 오류발생
         */
//        Member fetchOne = queryFactory
//                .selectFrom(member)
//                .fetchOne();

        /**
         * fetchFirst()을 사용한 단 건 조회, 조회한 첫 번째 값을 가져옴
         */
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        /**
         * 페이징 정보 포함.
         * total count query 추가 실행
         */
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
        results.getTotal();
        List<Member> content = results.getResults();
        /**
         * 페이징에 사용되는 여러 정보 조회가능
         */
//        results.getLimit();
//        results.getOffset();
//        results.getClass();
//        ...

        /**
         * count query로 변경해서 count 수를 조회한다.
         */
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    /**
     * ======정렬예제======
     * 회원 정렬별 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(null last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
//                .orderBy(member.age.desc(), member.username.asc().nullsfirst())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    /**
     * Paging 예제
     */
    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(2) //offset 자체는 0부터 시작한다. 그러나 2로 설정했기 때문에 조회한 값에서 2index부터 조회한다.
                .limit(2) //수량만큼 조회한다. 예제 기준 최대 2건을 조회한다는 의미이다.
                .fetch();

        assertThat(result.size()).isEqualTo(2);

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    /**
     * Paging 예제2(각 값이 어떻게 나올지 추측하고 맞는지 확인해보자)
     */
    @Test
    public void paging2() {
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    /**
     * 집합 함수 예제
     */

    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 1.group by 예제
     * 2.팀의 이름과 각 팀의 평균 연령을 구해라
     * 3.having(조건절)도 추가 가능하다
     */
    @Test
    public void group() throws Exception {
        //given
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();
        //when
        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);
        //then
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15); //(10 + 20) / 2

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35); //(30 + 40) / 2
    }

    /**
     * 1.join-기본 예제
     * 2.팀 A에 소속된 모든 회원
     */
    @Test
    public void join() {
        List<Member> result = queryFactory
                .selectFrom(member)
//                .innerJoin()
//                .leftJoin()
//                .rightJoin()
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();
        /**
         * username만 추출해서 containsExcatly로 문자열이 포함되어있는지 확인하는 junit
         */
        assertThat(result).extracting("username").containsExactly("member1", "member2");
    }

    /**
     * 1.세타 조인 예제
     * 2.회원의 이름이 팀 이름과 같은 회원 조회할 경우
     */
    @Test
    public void theta_join() throws Exception {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        //when
        List<Member> result = queryFactory
                .select(member)
                /**
                 * 세타 조인은 이렇게 나열만 하면 된다.
                 */
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();
        //then
        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * 1.On 예제
     * 2.회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: select mmt from Member m left join m.tam t on t.name = "teamA"
     * <p>
     * 정리
     * left join을 사용해야하는 경우라면 on절을 사용하고 그게아니라면 where절 사용하자
     * inner join에서 on절은 어차피 의미가 없다. inner join 자체가 교집합을 의미하는거기때문이다.
     * 그래서 inner join을 사용할 때는 바로 where절을 사용하면 된다.
     */
    @Test
    public void join_on_filtering() {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }


    /**
     * 연관관계 없는 엔터티 외부 조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조
     */
    @Test
    public void join_on_no_relation() throws Exception {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));
        //when
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        //then
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 패치 조인 예제
     */
    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNoUseVersion() {
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        /**
         * getPersistenceUnitUtil을 사용하면 영속성에 로드가 된지 안됐는지 알 수 있다.
         * Team은 FecthType.Lazy로 설정되어있기 때문에 프록시가 들어가 있고 로드가 안되있는게 맞다.
         */
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("패치 조인 미적용").isFalse();
    }

    @Test
    public void fetchJoinUseVersion() {
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        /**
         * getPersistenceUnitUtil을 사용하면 영속성에 로드가 된지 안됐는지 알 수 있다.
         * Team은 FecthType.Lazy로 설정되어있기 때문에 프록시가 들어가 있고 로드가 안되있는게 맞다.
         */
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("패치 조인 미적용").isTrue();
    }

    /**
     * 서브쿼리 예
     */
    @Test
    public void subQuery1() {
        /**
         * subQuery 사용을 위해 별칭이 한 개 더 필요하기 때문에 한 개 더 만든다.
         */
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(40);
    }

    /**
     * subQuery에 GOE(>=) 사용한 예제
     */
    @Test
    public void subQueryGoe() {
        /**
         * subQuery 사용을 위해 별칭이 한 개 더 필요하기 때문에 한 개 더 만든다.
         */
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(40);
    }

    @Test
    public void subQueryIn() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                )).fetch();

        assertThat(result).extracting("age").containsExactly(20, 30, 40);
    }

    /**
     * select절 안에 사용해보는 subquery 예제
     */
    @Test
    public void SubQuerySelect() {
        QMember memberSub = new QMember("memberSub");
        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * Case 예제
     */
    @Test
    public void basicCase() {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * case 예제, CaseBuilder()를 사용해 좀 더 복잡한 경우 처리 가능
     */
    @Test
    public void complexCase() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21 ~ 30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 상수 예제
     */
    @Test
    public void constant() {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 문자 더하기 예제
     */
    @Test
    public void concat() {
        /**
         * username + age 만드는 예제
         * age는 정수형이기 대문에 문자로 바꾸는 .stringValue를 적어줘야 한다.
         */
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 결과 반환을 tuple로 할 경우 예제
     */
    @Test
    public void tupleProjection() {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }

    /**
     * JQPL을 사용해서 결과 반환을 DTO로 반환하는 예제
     */
    @Test
    public void findDtoByJPQL() {
        List<MemberDto> result = em.createQuery(
                "select new study.querydsl.dto.MemberDto(m.username, m.age) " +
                        "from Member m", MemberDto.class).getResultList();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 1.QueryDsl의 Projections.bean을 사용하여 DTO 반환하는 예제
     * 2.내부에서 setter를 통한 방식을 통해 값을 만들어준
     */
    @Test
    public void findDtoBySetter() {
        /**
         * querydsl이 조회하기전 기본생성자로 틀을 만들어야하기 때문에 DTO에는 기본 생성자가 항상 있어야 한다.
         */
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 1.QueryDsl의 Field 접근을 사용하여 DTO를 반환하는 예제
     */
    @Test
    public void findDtoByField() {
        /**
         * querydsl이 조회하기전 기본생성자로 틀을 만들어야하기 때문에 DTO에는 기본 생성자가 항상 있어야 한다.
         */
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 1.QueryDsl의 Constructor 접근을 사용하여 DTO를 반환하는 예제
     * 2.실제로 DTO의 생성자로 이동해서 출력문 찍어보면 생성자가 실행되는것을 알 수 있다.
     */
    @Test
    public void findDtoByConstructor() {
        /**
         * querydsl이 조회하기전 기본생성자로 틀을 만들어야하기 때문에 DTO에는 기본 생성자가 항상 있어야 한다.
         */
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }

//        List<UserDto> result = queryFactory
//                .select(Projections.constructor(UserDto.class,
//                        member.username,
//                        member.age))
//                .from(member)
//                .fetch();
//        for (UserDto memberDto : result) {
//            System.out.println("memberDto = " + memberDto);
//        }
    }

    /**
     * 1.DTO가 다르나 틀이 같을 경우 필드 값을 .as를 사용하여 별칭을 설정해줘야 한다.
     * 2.필드값이 다르면 조회가 불가능하다.
     */
    @Test
    public void findUserDto() {
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        member.age))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    /**
     * 서브쿼리를 조회하여 DTO값 한 개에 모든 값을 설정하는 예제.
     * 서브쿼리를 DTO에 select 영역에도 넣을 수 있다.
     */
    @Test
    public void findUserDtoAndSubQuery() {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),

                        ExpressionUtils.as(select(memberSub.age.max())
                                .from(memberSub), "age")
                ))
                .from(member)
                .fetch();
        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    /**
     * 1.@QueryProjection을 사용한 결과 반환
     * 2.조회 엔터티(DTO)에 @QueryProjection을 추가해야한다.
     * ps.distinct 필요시 그냥 select 뒤에 추가하면된다.
     */
    @Test
    public void findDtoByQueryProjection() {
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))//.distinct()
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 동적쿼리 생성 예제1 - Boolean builder를 사용해서 해결하기
     */
    @Test
    public void dynamicQuery_BooleanBuilder() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    /**
     * 동적 쿼리 예제 - 사람이름이 조건이 있는 경우 query 포함, 사람 나이가 있는 경우 조건 포함
     */
    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();
        /**
         * 아래와 같이 생성자에 query를 넣어서 초기값 설정도 가능하다.
         */
//        BooleanBuilder builder = new BooleanBuilder(member.username.eq(usernameCond));

        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    /**
     * 동적쿼리 생성 예제2 - where에서 다중 파라미터를 사용해서 해결하기
     */
    @Test
    public void dynamicQuery_whereParam() {
        String usernameParam = null;
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
//                .where(allEq(usernameCond,ageCond))
                .where(ageEq(ageCond), usernameEq(usernameCond))
                .fetch();
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond == null ? null : member.age.eq(ageCond);
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond == null ? null : member.username.eq(usernameCond);
    }

    /**
     * 둘 다 있는 경우만 값을 반환
     */
    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

    /**
     * 벌크 연산 예제1 - (대량 데이터 수정시)
     */
    @Test
    @Commit
    public void bulkUpdate() {
        /**
         * 1.현재 영속성 영역의 값
         */
        //member1 = 10 -> DB member1
        //member2 = 20 -> DB member2
        //member3 = 30 -> DB member3
        //member4 = 40 -> DB member4

        /**
         * excute는 실행된 개수의를 반환한다.
         */
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(31))
                .execute();
        System.out.println("count = " + count);
        /**
         * 2.쿼리 이후 영속성 영역의 값
         * **중요***
         * *****DB 값은 바뀌어도 영속성 영역의 값은 바뀌지 않는다.****
         * 그러므로 em.flush, em.clear 는 벌크쿼리 이후 필수다.
         */
        //member1 = 10 -> DB member1
        //member2 = 20 -> DB member2
        //member3 = 30 -> DB member3
        //member4 = 40 -> DB member4

        em.flush();
        em.clear();
        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();
        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    /**
     * 벌크 연산 예제2 - 전체 각 나이 + 1
     */
    @Test
    public void bulkAdd() {
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
//                .set(member.age, member.age.multiply(2))
//                .set(member.age, member.age.$:연산자명령(2))
                .execute();
    }

    /**
     * 벌크 연산 예제3 - 특정 데이터값 기준 삭제
     */
    @Test
    public void bulkDelete() {
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }

    /**
     * Sql function 호출 예제
     * function의 첫 번째 매개변수는 각 DB에 존재하는 function 이름을 사용해야 한다.
     */
    @Test
    public void sqlFunction() {
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace', {0}, {1}, {2})",
                        member.username, "member", "M"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
        ;
    }

    @Test
    public void sqlFunction2(){
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                /**
                 * 대문자만 출력하고 싶은 경우
                 */
                .where(member.username.eq(member.username.upper()))
//                .where(member.username.eq(
//                        Expressions.stringTemplate("function('upper', {0})", member.username)))
//                          Expressions.stringTemplate("function('lower', {0})", member.username)))
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

}
