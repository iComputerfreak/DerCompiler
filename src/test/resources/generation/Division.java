class Division {

    public static void main(String[] args) {
        Division division = new Division();
        int d = division.divide(25, 7);
        System.out.println(d);
        System.out.println(555 / 11);
        int e = division.mod(25, 7);
        System.out.println(e);
        System.out.println(40 % 14);
    }

    public int divide(int dvd, int dsr) {
        return dvd / dsr;
    }

    public int mod(int dvd, int dsr) {
        return dvd % dsr;
    }
}