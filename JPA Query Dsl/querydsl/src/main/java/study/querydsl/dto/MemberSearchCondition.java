package study.querydsl.dto;

import lombok.Data;

/**
 * member 찾는 조건 DTO.
 * 있을 경우 첨부하고 없으면 첨부안하는 형식으로 진행
 * 순서별호 회원명, 팀명, 나이정렬기준(크거나같거나, 작거나 같거나)
 */
@Data
public class MemberSearchCondition {
    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
