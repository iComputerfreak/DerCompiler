class Nulls2 {

    public static void main(String[] args) {
        Nulls2[] t = new Nulls2[2];    /* Einträge haben beim Laden die Scale 0 */

        t[0] = new Nulls2();

        int i = 0;
        while (i < 1000) {
            Nulls2 temp = t[0];
            t[0] = t[1];
            t[1] = temp;
            i = i + 1;
        }
        if (t[0] != null) t[0].test();
    }

    public void test() {
        System.out.println(42);
    }
}
