package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
//상속 관계이기 때문에 테이블전략을 선언해줘야 한다. 싱글로 할지 정규화로 할지
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//상속받은 객체마다 구분값
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     * 비즈니스로직을 만들때 해당 타겟의 변동성이 있는 곳에 직접 만드는것이 좋다.
     * 예를 들어 stockQuantity를 줄이고 늘리는 로직은 stockQuantity 필드가 있는 클래스에 만드는것이 좋다.
     */
    //==비즈니스 로직==//
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소 로직
     * 만약 0개 이하라면 예외 발생을 시킨다
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
