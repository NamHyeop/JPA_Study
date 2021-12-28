package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class JpaBaseEntity {
    /**
     * create 데이터는 업데이트 못한게 막아 놓았다.
     */
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * @PrePersist를 사용하여 객체 생성 때 생성날짜와 업데이트 날짜를 현재 시간 기준으로 설정
     */
    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        //this 생략, IDE가 색칠 해주니까
        createdDate = now;
        updatedDate = now;
    }

    /**
     * @PreUpdate를 사용하여 UpdateQuery발생시 업데이트 날짜를 현재 시간으로 설정한다.
     */
    @PreUpdate
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}
