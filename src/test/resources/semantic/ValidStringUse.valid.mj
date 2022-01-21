/* OK */

class Main {
    public String y;

    public String foo(String arg) {
        y = arg;
        return foo(arg);
    }

    public static void main(String[] args) {
        Main m = new Main();
        String x = m.foo(null);
        m.foo(x);
    }
}
