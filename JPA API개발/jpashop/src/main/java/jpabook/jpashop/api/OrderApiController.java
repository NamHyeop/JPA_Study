package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        /**
         * 실행해보면 중복 되어서 값이 생기는것을 확인 가능
         * distinct로 해결할 수 있다.(중복 확인을 위해서는 findAllWithItem으로 이동해서 query문의 distinct를 제거해야한다)
         */
//        for(Order order : orders){
//            System.out.println("order ref = " + order + " id=" + order.getId());
//        }
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    /**
     * 실행된 쿼리를 DB에 돌려보면 V3와 다르게 중복이 없음을 확인할 수 있다.
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    /**
     * V3.1은 ToOne처럼 데이터 뻥튀기가 안일어나는 경우만 했다면 이번에는
     * 데이터 뻥튀기가 일어나는 ToMany경우의 조회를 진행해본다.
     */

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    /**
     * V4에서 콜렉션을 join으로 작성하여 쿼리가 여러개가 나가 최적화를 완벽화하지 못했다.
     * V5에서는 이 과정을 진행한다.
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.finAllByDto_optimization();
    }

    /**
     * V5에서는 쿼리가 두번 날아가는데 V6에서는 쿼리수 한 번으로 줄여본다.
     * V6에서는 OrderQueryDto랑 OrderItemQueryDto의 정보를 모두 담고 있는 OrderQueryDto를 만든다.
     * 이후 OrderQueryDto랑 OrderItemQueryDto의 집합(map)을 만들어서 중복을 제거한다.
     * 그러나 메모리에서 작업해서 매핑을 통해 반환하는 JSON 데이터에 중복을 제거해준것이기 때문에 실제 테이블의 중복이 제거된것이 아니기 때문에 페이징을 불가능하다.
     * 대신 조회는 쿼리 한 번이라 데이터가 적을때는 유용하다.
     * @return
     */
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream().collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        /**
         * 이게 V2의 문제임
         * V1은 Entity가 뻔히 보여서 문제가 바로 보이는데 이녀석은 DTO안에 Colletion이 들어가 있어서
         * 모든 정보가 노출됨.
         * 결론적으로 Entity가 노출되는 V1의 문제점이 또 발생하는것이다.
         * 해결방법은 똑같이 Collection의 DTO를 또 만들어줘야 한다.
         */
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            /**
             * Collect의 모든 정보가 노출되는 방식, DTO를 사용안했기 때문이다
             */
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto{

        private String itemName;//상품 명
        private int orderPrice;//주문 가격
        private int count; //주문 수량
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
