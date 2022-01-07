package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {
    //select m from Member m where m.usernmae=? -> 메소드 이름으로 매칭하는 방식
    List<Member> findByUsername(String username);

}
