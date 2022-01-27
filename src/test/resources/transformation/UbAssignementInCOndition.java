class a {
    public static void main(String[] args) {
        boolean b = false;
        boolean c;
        b = false || (c = true);
        new a().print(c);
    }

    public void print(boolean b) {
        if (b) System.out.println(4); else System.out.println(5);
    }
}