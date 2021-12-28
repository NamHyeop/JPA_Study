package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            /**
             * JPQL 작성 예제
             * From절에 Member가 객체 Entity Member를 가리키는것을 알 수 있다.
             * m을 가리키는것은 member m 자체를 가져오라는 의미이다.
             */
//            List<Member> result = em.createQuery("select m From Member m where m.username like '%kim%'", Member.class).getResultList();
//            for (Member member : result) {
//                System.out.println("member = " + member);

            /**
             * JPA Criteria 작성 예제
             * 1. 자바 코드라 컴파일에 좋다.
             * 2. 단점은 사용하기 어렵다.
             * 3. 걍 이거말고 뒤에서 배우는 QueryDSL 사용하자, JPA Critera는 실무에서 안쓰고 유지 보수하기 힘들다.
             */
//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Member> query = cb.createQuery(Member.class);
//
//            Root<Member> m = query.from(Member.class);
//            CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
//            List<Member> resultList = em.createQuery(cq).getResultList();
//            tx.commit();

            /**
             * QueryDSL 사용 예제
             * 예제 정보가 부족해서 실행은 안됨
             */
//            JPAFactoryQuery query = new JPAQueryFactory(em);
//            QMember m = QMember.member;
//            List<Member> list =
//                    query.selectFrom(m)
//                            .where(m.age.gt(18))
//                            .orderBy(m.name.desc())
//                            .fetch();

            /**
             * Nativi Query 작성 예제(그냥 쌩 날 sql 코드이다)
             * cretaeNativeQuery가 동작할때는 flush가 작동하고 실행된다. 밑에 예제에서 보면 commit을 안해도 query가 날아가는것을 볼 수 있다.
             * createNativeQuery 사용할 때 두번째 매개변수 설정할 때 자신의 Entity가 어느것인지 명시해주는걸 까먹지 말자
             */
//            Member member = new Member();
//            member.setUsername("flushExample");
//            em.persist(member);

            /**
             * 이런 경우에는 수동 플러쉬가 필요하다.
             * dbconn.executeQuey("select ~~~~~")
             * em.flush();
             */

//            List<Member> resultList = em.createNativeQuery("select MEMBER_ID, city, street, zipcode, USERNAME from MEMBER", Member.class).getResultList();
//            for (Member m : resultList) {
//                System.out.println("m = " + m);
//            }

            tx.commit();
        }catch(Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }

        emf.close();

    }

}
