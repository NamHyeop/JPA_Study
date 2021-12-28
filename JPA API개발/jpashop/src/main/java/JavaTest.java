import lombok.AllArgsConstructor;

@AllArgsConstructor
class A{
    int testA;
    int testB;

}

public class JavaTest {
    public static void main(String[] args) {
        String name = "my name is namhyeopKim";

       A testA = new A(1, 2);
       A testB = new A(3, 4);
        System.out.println("testA = " + testA.testA);
        System.out.println("testB = " + testB.testA);
    }
}
