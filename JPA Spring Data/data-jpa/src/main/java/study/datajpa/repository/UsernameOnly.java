package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

/**
 * 1.Projections 예제
 * 2.Entity에서 오직 username만 가져오는 예시
 */
public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
