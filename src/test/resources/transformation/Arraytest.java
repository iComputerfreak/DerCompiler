class Main {
    public Main[][] data;
    public int v;

    public Main init(int v) {
        this.v = v;
        return this;
    }

    public int foo() {
        return v;
    }
    public static void main(String[] args) {
        Main main = new Main();
        main.data = new Main[3][];
        main.data[2] = new Main[3];
        main.data[2][0] = new Main().init(1);
        main.data[2][1] = new Main().init(2);
        main.data[2][2] = new Main().init(3);

        int sum = 0;
        sum = sum + main.data[2][0].foo();
        sum = sum + main.data[2][1].foo();
        sum = sum + main.data[2][2].foo();
        System.out.println(sum);
    }
}
