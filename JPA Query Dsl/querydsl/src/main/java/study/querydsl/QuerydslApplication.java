package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class QuerydslApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuerydslApplication.class, args);
	}

	/**
	 * JPQQueryFactory를 빈에 추가하는 방법
	 * 이렇게 하면 Repository에서
	 * public memberjparepository(JPAQueryFactory queryFactory){
	 *     this.em = em;
	 *     this.queryFactory = queryFactory
	 * }
	 * 같은 형식으로 등록이 가능함
	 */
//	@Bean
//	JPAQueryFactory jpaQueryFactory(EntityManager em){
//		return new JPAQueryFactory(em);
//	}
}
