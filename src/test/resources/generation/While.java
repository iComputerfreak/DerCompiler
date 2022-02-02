class While {

    public static void main(String[] args) {
        int i = 0;
        int even = 0;
        int odd = 0;
        while (i < 10) {
            if ((i / 2) * 2 == i) {
                even = even + i;
            } else {
                odd = odd + 1;
            }

            i = i + 1;
        }
        System.out.println(even);
        System.out.println(odd);
    }
}