
package jpql;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            /**
             * 예제 확인을 위한 데이터 추가
             */
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("회원4");
            em.persist(member4);
            em.flush();
            em.clear();

            /**
             * 벌크 연산 예제
             * 모든 Member의 나이름 20으로 변경
             * 반환값은 변경된 엔터티의 수이다.
             */
//            int resultCount = em.createQuery("update Member m set m.age = 20 where m.username = '회원1'")
//                    .executeUpdate();
//            System.out.println("resultCount = " + resultCount);

            /**
             * 벌크 연산 주의 예제
             */
            int resultCount = em.createQuery("update Member m set m.age = 20").executeUpdate();
            System.out.println("resultCount = " + resultCount);
            //em.clear(); 호출 타이밍에서 em.getRefernce랑 착각 하지말자 이거 때문에 시간낭비함
            //member1 = em.find(Member.class, member1.getId());
            /**
             * DB에만 업데이트 되어서 값이 변경 안되어있는것을 확인할 수 있다.
             */
            System.out.println("member1 = " + member1.getAge());
            System.out.println("member2 = " + member2.getAge());
            System.out.println("member3 = " + member3.getAge());

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
