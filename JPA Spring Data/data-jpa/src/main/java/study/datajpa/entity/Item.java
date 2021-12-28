package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Item{
//    @Id @GeneratedValue
//    private Long id;
//
//    /**
//     * JPA 기본 생성자 @NoArgsConstructor로 대체
//     * @param id
//     */
////    protected Item(){
////    }
////
//
//    /**
//     * 1.생성장자를 @GeneratedValue로 설정안하고 직접해주고 싶을 때.
//     * 2.하지만 이런 방식을 사용하게 되면 객체를 생성할 때 기본 값을 넣어주게 되고 SimpleJpaRepository의 Save에서
//     * NULL로 인식을 안 해서 Merge가 발생하는 분류 쪽으로 넘어감. 성능적으로도 손해이고 Dirty checking을 사용 안하므로 좋은 방식이 아니다.
//     * @param id
//     */
////    public Item(String id){
////        this.id = id;
////    }
//}

/**
 * 1.만약 대규모 프로젝트나 사정이 생겨 @GeneratedValue를 사용 못하게 될 경우 직접 생성자를 넣어줘야 할 때 라면
 * 2.@CreatedDate를 사용해서 isNew조건에 구현해주면 된다.
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id){
        this.id = id;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}

