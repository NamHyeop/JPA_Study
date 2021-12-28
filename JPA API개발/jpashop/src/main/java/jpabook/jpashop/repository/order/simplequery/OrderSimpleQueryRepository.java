package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;
    /**
     * API 조회 V4-성능 최적화 예제
     */
    public List<OrderSimpleQueryDto> findOrderDtos() {
        /**
         * 예제에서 o는 OrderSimpleQueryDto.class이다.
         * o 자체를 넘기면 o의 식별자만 넘어가게 된다. 그래서 DTO에 정의된 필드가 전부 넘어가지 않는다.
         * 값 value는 예외이다. 값 value는 그자체가 넘어간다.
         * 그러므로로 DTO 객체에 생성자를 정의해줘야 한다.
         */
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
