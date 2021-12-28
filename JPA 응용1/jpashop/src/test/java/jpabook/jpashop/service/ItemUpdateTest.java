package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception{
        Book book = em.find(Book.class, 1L);

        /**
         * Transaction이 된다. 왜냐하면 JPA가 Dirty checking을 해서 값의 변경을 알아차리고
         * 자동으로 업데이트해준다.
         */
        book.setName("바꿔주기만 해도 업데이트 되지롱");

    }
}
