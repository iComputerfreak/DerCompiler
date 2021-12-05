class Main {

    public int foo() {
        boolean b = true;
        int x = 50;
        int y = -50;
        while (x > 20 && y != 229) {
            x = x - 1;
            if ((b = (x % 5 != 0))) {
                System.out.println(x);
            } else {
                x = ((x + y + 38) % 75) + 20;
                y = y + 9;
                System.out.println(y);
            }
        }
        return x + y;
    }

    public static void main(String[] main) {
        Main bar = new Main();
        System.out.println(bar.foo());
        System.out.flush();
    }

}
