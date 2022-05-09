package jpabook.jpashop.domain;

import jpabook.jpashop.domain.Item;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
public class Category extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;

    //부모 카테고리를 조인한 것(이게 뭐지?)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Category parent;

    //카테고리 정보들이 일렬로 되기위해 있는 mappe (이게뭐지?)
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    @ManyToMany
    @JoinTable(name ="CATEGORY_ITEM",
                joinColumns = @JoinColumn(name = "CATEGORY_ID"),
                inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private List<Item> items = new ArrayList<>();
}
