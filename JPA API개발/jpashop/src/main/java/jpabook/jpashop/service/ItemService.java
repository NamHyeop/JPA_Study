package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    /**
     * 변경 감지를 활용한 Dirty checking
     * updateItem에 매개변수가 너무 많은거 같으면 UpdateItemDTO를 하나 만들어서 DTO를 넘기는것도 좋은 방법이다.
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        Item Item = itemRepository.findOne(itemId);
        Item.setName(name);
        Item.setPrice(price);
        Item.setStockQuantity(stockQuantity);
    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
