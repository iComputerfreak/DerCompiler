class Mul2 {

    public static void main(String[] args) {
        new Mul2().mul(13, 3);
    }

    public void mul(int f1, int f2) {
        /* should use mul2 b/c operands are registers */
        System.out.println(f1 * f2);
    }
}