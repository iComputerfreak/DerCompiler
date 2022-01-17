class AllocEmpty {

    public boolean thisIsNull() {
        return this == null;
    }

    public static void main(String[] args) {
        /* These tests will fail on some malloc implementations. If you are
         * lucky they work even if you do not implement it correctly, but I can
         * not detect that. */
        AllocEmpty foo = new AllocEmpty();

        if (foo.thisIsNull()) {
            System.out.write(110);
            System.out.write(101);
            System.out.write(119);
            System.out.write(32);
            System.out.write(79);
            System.out.write(98);
            System.out.write(106);
            System.out.write(101);
            System.out.write(99);
            System.out.write(116);
            System.out.write(40);
            System.out.write(41);
            System.out.write(32);
            System.out.write(111);
            System.out.write(102);
            System.out.write(32);
            System.out.write(99);
            System.out.write(108);
            System.out.write(97);
            System.out.write(115);
            System.out.write(115);
            System.out.write(32);
            System.out.write(119);
            System.out.write(105);
            System.out.write(116);
            System.out.write(104);
            System.out.write(111);
            System.out.write(117);
            System.out.write(116);
            System.out.write(32);
            System.out.write(102);
            System.out.write(105);
            System.out.write(101);
            System.out.write(108);
            System.out.write(100);
            System.out.write(115);
            System.out.write(32);
            System.out.write(40);
            System.out.write(116);
            System.out.write(97);
            System.out.write(107);
            System.out.write(105);
            System.out.write(110);
            System.out.write(103);
            System.out.write(32);
            System.out.write(117);
            System.out.write(112);
            System.out.write(32);
            System.out.write(122);
            System.out.write(101);
            System.out.write(114);
            System.out.write(111);
            System.out.write(32);
            System.out.write(98);
            System.out.write(121);
            System.out.write(116);
            System.out.write(101);
            System.out.write(115);
            System.out.write(41);
            System.out.write(32);
            System.out.write(114);
            System.out.write(101);
            System.out.write(116);
            System.out.write(117);
            System.out.write(114);
            System.out.write(110);
            System.out.write(101);
            System.out.write(100);
            System.out.write(32);
            System.out.write(97);
            System.out.write(32);
            System.out.write(110);
            System.out.write(117);
            System.out.write(108);
            System.out.write(108);
            System.out.write(32);
            System.out.write(112);
            System.out.write(111);
            System.out.write(105);
            System.out.write(110);
            System.out.write(116);
            System.out.write(101);
            System.out.write(114);
            System.out.write(33);
            System.out.write(10);
        }

        int[] arr = new int[0];
        if (arr == null) {
            System.out.write(110);
            System.out.write(101);
            System.out.write(119);
            System.out.write(32);
            System.out.write(105);
            System.out.write(110);
            System.out.write(116);
            System.out.write(91);
            System.out.write(48);
            System.out.write(93);
            System.out.write(32);
            System.out.write(114);
            System.out.write(101);
            System.out.write(116);
            System.out.write(117);
            System.out.write(114);
            System.out.write(110);
            System.out.write(101);
            System.out.write(100);
            System.out.write(32);
            System.out.write(97);
            System.out.write(32);
            System.out.write(110);
            System.out.write(117);
            System.out.write(108);
            System.out.write(108);
            System.out.write(32);
            System.out.write(112);
            System.out.write(111);
            System.out.write(105);
            System.out.write(110);
            System.out.write(116);
            System.out.write(101);
            System.out.write(114);
            System.out.write(33);
            System.out.write(10);
        }
    }
}
