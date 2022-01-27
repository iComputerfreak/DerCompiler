class Swap {

    public static void main(String[] args) {
        int i = 1; int j = 2; int k = 3; int l = 4; int m = 5;
        while (i < 1000) {
            int t = i;
            i = j;
            j = k;
            k = t + 1;

            t = m;
            m = l;
            l = t;

        }

        System.out.println(j + m);
    }
}