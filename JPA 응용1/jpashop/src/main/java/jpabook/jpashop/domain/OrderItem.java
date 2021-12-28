package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
/**
 * 생성자가 없는 경우 생성 접근 권한을 PROTECTED로 설정함
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;

    /**
     *     기본 생성자보다 롬복중 @NoArgosConstructor을 활용하는것이 좀 더 가독성이 높다
     */
//    protected OrderItem(){ }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        //아이템을 생성하는 동시에 아이템의 재고도 줄여준다.
        item.removeStock(count);
        return orderItem;
    }
    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }


    //==조회 로직==//
    public int getTotalPrice(){
        return getOrderPrice() * getCount();
    }
}
