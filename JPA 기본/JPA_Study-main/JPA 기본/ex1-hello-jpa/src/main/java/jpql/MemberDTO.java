package jpql;

public class MemberDTO {
    private String username;
    private int age;

    //생성자를 사용해 MeberDTO를 조회에 이용한다
    public MemberDTO(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
