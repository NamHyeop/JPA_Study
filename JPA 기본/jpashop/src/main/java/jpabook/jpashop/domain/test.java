package jpabook.jpashop.domain;

public class test {
    public static void main(String[] args) {
        int a = 10;
        int b = a;
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        Long aLong = new Long(10L);
        Long bLong = aLong;
        aLong = 20L;
        System.out.println("aLong = " + aLong);
        System.out.println("bLong = " + bLong);

        System.out.println("================== " );
        passODT passODT1 = new passODT(500);
        passODT passODT2= passODT1;
        System.out.println("passODT1 = " + passODT1.getValue());
        System.out.println("passODT2 = " + passODT2.getValue());
        System.out.println("=========befoe======");
        passODT1.setValue(300);
        System.out.println("passODT1 = " + passODT1.getValue());
        System.out.println("passODT2 = " + passODT2.getValue());
    }

    public static class passODT{
        int value;

        public passODT(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
