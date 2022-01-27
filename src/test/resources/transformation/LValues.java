class LValues {
    public static void main(String[] args) {
        int a = 42;
        a = 100;

        int[] b = new int[3];
        b[0] = b[1] = b[2] = 1;

        int[][][] tensor = new int[3][][];
        tensor[0] = new int[3][];
        tensor[0][0] = new int[3];
        tensor[0][0][0] = 42;

        Tup t = new Tup();
        t.fst = 25;
        t.snd = 17;
    }
}

class Tup {
    public int fst;
    public int snd;
}
