class Division {
    public static void main(String[] args) {
        new Division().printDiv(-2147483648, -1);
        new Division().printMod(-2147483648, -1);
    }

    public void printMod(int a, int b) {
        System.out.println(a % b);
    }

    public void printDiv(int a, int b) {
        System.out.println(a / b);
    }
}
