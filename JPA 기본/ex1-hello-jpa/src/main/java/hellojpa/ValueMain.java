package hellojpa;

public class ValueMain {
    public static void main(String[] args) {
        int a = 10;
        int b = 10;

        System.out.println("a == b : " + (a == b));

        Address address1 = new Address("city", "street", "10000");
        Address address2 = new Address("city", "street", "10000");

        System.out.println("address1 == address2 : " + (address1 == address2)); // false
        // 오버라이딩 재정의 안하면 false 왜냐하면 equals의 기본 비교 연산이 this이기 때문이다.
        System.out.println("address1 equals address2 : " + (address1.equals(address2)));

    }

}
