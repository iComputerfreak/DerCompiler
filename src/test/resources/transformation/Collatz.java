class Collatz {

    public static void main(String[] args) {
        int i = 12345678;
        int j = 0;
        while (i != 1) {
            j = j + 1;
            if ((i / 2) * 2 == i) {
                i = i / 2;
            } else {
                i = 3 * i + 1;
            }
        }
    }
}