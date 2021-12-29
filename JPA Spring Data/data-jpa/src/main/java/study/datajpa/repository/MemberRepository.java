package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * NamedQuery를 사용해서 특정 query를 날리는 방법
     * param은 NamedQuery에서 등록해서 넘길 변수이름을 등록해야할 때 사용한다.
     * 현재 예제 기준으로는 username이다.(Member의 @NamedQuery 확인해보면 알 수 있다.)
     * @return
     */

    /**
     * 생략 가능
     * 관례상 현재엔터티(Meber).현재 메소드(findByUsername) 로 검색하기 때문이다.
     */
    //@Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * repository 메소드에 쿼리를 정의하는 방법
     * 실무에서 가장 많이 쓰인다.
     * 장점이 오타를 쳐도 사전에 쿼리문이 작성되는 과정에서 오류를 발생시켜주기 때문에 좋다.
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    /**
     * DTO를 조회하는 Query
     */
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * Param이 Collection 타입으로 들어오는 경우
     */

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);
    /**
     * 상위 Collection을 받고 싶을때는 아래처럼 수정
     */
    //List<Member> findByNames(@Param("names")Collection<String> names);

    /**
     * Spring Data Jpa의 리턴값들. 종류가 이것보다 더 많다.
     */
    List<Member> findListByUsername(String name);

    Member findMemberByUsername(String name);

    Optional<Member> findOptionalByUsername(String name);

    /**
     * Spring DATA JPA 페이징 정렬 예제
     * totalCont 같이 DB의 join을 시도하여 많은 수를 조회하는 방식은 성능 이슈를 불러 오기 때문에
     * 아래처럼  CountQuery를 지정해주는것이 좋다.
     */

    @Query(value = "select  m from Member m",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * slice 예제
     */
    //Slice<Member> findByAge(int age, Pageable pageable);

    /**
     * 벌크성 쿼리 예제
     */

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * EntityPath 예제
     */
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    /**
     * Query 사용에 패치조인을 하고 싶으면 아래처럼 @EntityGraph를 추가해주면 된다.
     */
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    /**
     * 메소드로 받아오는 형식에서 Team정보도 자주 사용할 꺼 같은 경우 @EntityGraph를 사용해서 패치 조인 할 수 있다.
     */
    @EntityGraph(attributePaths = ("team"))
    //@EntityGraph("Member.all") -> entity(Member.java)에 정보를 등록하고 사용도 가능한데 굳이.. JPA 표준 스펙인걸로만 알고 있으면 될거 같다.
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * hint 사용 예제
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * Lock 사용 예제
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    /**
     * Projections 예제
     */
    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);
    List<Age1OnlyDto> findProjectionsByAge(@Param("age") int age);

    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    /**
     * NativeQuery 예제
     */
    @Query(value = "select * from Member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    /**
     * Spring Data JPA Native Query + 인터페이스 기반 Projections 활용한 예제
     */
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}