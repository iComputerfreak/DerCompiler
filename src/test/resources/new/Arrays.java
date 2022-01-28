class Main {
    public A zero;
    public A[] one;
    public A[][] two;
    public A[][][] three;

    public void init() {
        zero = new A();
        zero.a = 1;

        one = new A[1];
        one[0] = new A();
        one[0].a = 2;

        two = new A[1][];
        two[0] = new A[1];
        two[0][0] = new A();
        two[0][0].a = 3;

        three = new A[1][][];
        three[0] = new A[1][];
        three[0][0] = new A[1];
        three[0][0][0] = new A();
        three[0][0][0].a = 4;
    }

    public int foo() {
        return zero.a + one[zero.a - 1].a + two[0][zero.a - 1].a + three[0][0][0].a - 10;
    }

    public void bar() {
        while (three[0][0][0].a > 0) {
            System.out.println(0);
            one[0].a = 1;
            three[0][0][0].a = three[0][0][0].a - (two[0][0].a - one[0].a);
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.init();
        System.out.println(m.foo());

        m.bar();
    }
}

class A {
    public int a;
}