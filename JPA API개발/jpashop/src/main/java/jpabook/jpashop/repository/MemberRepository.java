package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //아래 쿼리가 실행된다.
    //select m form Member m wher m.name = ?
    List<Member> findByName(String name);
}
