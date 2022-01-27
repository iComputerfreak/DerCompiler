class Main {

    public static void main(String[] args) {
        Main main = new Main();
        main.test1();
        main.test2();
        main.test3();
    }

    public void test3() {
        Z z = new Z();
        z.consume(z.createSequence(5));
    }

    public void test2() {
        X x = new X();
        Y y = new Y();
        (x.y = y).x = x;

        x.y.init(0);
        x.y.x.init(0);

        System.out.println(x.foo(5));
        System.out.println(y.foo(5));

        int i = 0;
        int sum = 0;
        while(i < 5) {
            sum = sum + (x.y.x.y.x.y.b[i] - y.x.y.x.a[i]);
            i = i + 1;
        }
        System.out.println(sum);
    }

    public void test1() {
        X x = new X().init(0);
        Y y = new Y().init(0);
        x.y = y;
        y.x = x;

        System.out.println(x.foo(5));
        System.out.println(y.foo(5));
    }

}

class X {
    public Y y;
    public int[] a;

        public X init(int base) {
        a = new int[6];
        int i = 0;
        while (i < 6) {
            a[i] = base + i;
            i = i + 1;
        }
        return this;
    }

    public int foo(int v) {
        if (v == 0) {
            return 0;
        }
        return a[v] + y.foo(v);
    }
}

class Y {
    public X x;
    public int[] b;

    public Y init(int base) {
        b = new int[6];
        int i = 0;
        while (i < 6) {
            b[i] = base + 6 - i;
            i = i + 1;
        }
        return this;
    }

    public int foo(int v) {
        return b[v] + x.foo(v - 1);
    }
}

class Z {

    public X createSequence(int len) {
        X x = new X().init(len);
        x.y = new Y().init(len);
        x.y.x = x;

        if (len > 1)

        while(len > 1) {
            len = len - 1;
            X tmp = x;
            ((x = new X().init(len)).y = new Y().init(len)).x = tmp;
        }

        return x;
    }

    public void consume(X x) {
        int i = 0;
        while(i < 5) {
            System.out.println(x.foo(5) - 60);
            x = x.y.x;
            i = i + 1;
        }
    }

    public void consume2(X x1, X x2) {
        x1.y.x.y.x = x2;
        
    }
}
