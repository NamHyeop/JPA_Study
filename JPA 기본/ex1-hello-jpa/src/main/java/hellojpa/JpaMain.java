package hellojpa;

import org.h2.command.ddl.AlterTableDropConstraint;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
//            Address address = new Address("city", "street", "10000");
//
//            //1.이런 방식의 address를 넘기는 것은 굉장히 위험하다. 리퍼런스가 하나만 보고 있기 때문이다.
//            Member member1 = new Member();
//            member1.setUsername("member1");
//            member1.setHomeAddress(address);
//            em.persist(member1);
//
//
//            Member member2 = new Member();
//            member2.setUsername("member2");
//            member2.setHomeAddress(address);
//            em.persist(member2);
//
//            member1.getHomeAddress().setCity("you see that changed two file");
//            //=================================================================
//            //2.밑에 방식처럼 new를 통한 객체를 생성해서 새로운 객체를 따로 만들어서 넘겨줘야 한다.
//            Address address2 = new Address("city", "street", "10000");
//
//            Member member3 = new Member();
//            member3.setUsername("member3");
//            member3.setHomeAddress(address2);
//            em.persist(member3);
//            Address copyAddress2 = new Address(address2.getCity(), address2.getStreet(), address2.getZipcode());
//
//            Member member4 = new Member();
//            member4.setUsername("member2");
//            member4.setHomeAddress(copyAddress2);
//            em.persist(member4);
//            member3.getHomeAddress().setCity("you see that change situation of only one file");

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
