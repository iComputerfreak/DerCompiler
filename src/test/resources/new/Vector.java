class vec3 {
    public int[] data;

    public vec3 init(int x, int y, int z) {
        data = new int[3];
        data[0] = x;
        data[1] = y;
        data[2] = z;
        return this;
    }

    public vec3 fromArray(int[] values) {
        return init(values[0], values[1], values[3]);
    }

    public int getX() {
        return data[0];
    }

    public int getY() {
        return data[1];
    }

    public int getZ() {
        return data[2];
    }

    public int[] getData() {
        return data;
    }

    public int product(vec3 other) {
        return getX() * other.getX() + getY() * other.getY() + getZ() * other.getZ();
    }

    public vec3 scale(int i) {
        return new vec3().init(i * getX(), i * getY(), i * getZ());
    }

    public int sum() {
        return getX() + getY() + getZ();
    }

    public void print() {
        System.out.println(getX());
        System.out.println(getY());
        System.out.println(getZ());
    }
}

class mat3x3 {
    /*
     * datalayout:
     * 
     *   U V W
     * X . . .
     * Y . . .
     * Z . . .
     *  
    */
    public int[][] data;

    public mat3x3 init(int ux, int uy, int uz, int vx, int vy, int vz, int wx, int wy, int wz) {
        data = new int[3][];
        data[0] = new int[3];
        data[1] = new int[3];
        data[2] = new int[3];
        data[0][0] = ux;
        data[0][1] = uy;
        data[0][2] = uz;
        data[1][0] = vx;
        data[1][1] = vy;
        data[1][2] = vz;
        data[2][0] = wx;
        data[2][1] = wy;
        data[2][2] = wz;
        return this;
    }

    public int[] getX() {
        int[] tmp = new int[3];
        tmp[0] = data[0][0];
        tmp[1] = data[1][0];
        tmp[2] = data[2][0];
        return tmp;
    }

    public int[] getY() {
        int[] tmp = new int[3];
        tmp[0] = data[0][1];
        tmp[1] = data[1][1];
        tmp[2] = data[2][1];
        return tmp;

    }

    public int[] getZ() {
        int[] tmp = new int[3];
        tmp[0] = data[0][2];
        tmp[1] = data[1][2];
        tmp[2] = data[2][2];
        return tmp;
    }

    public int[] getU() {
        return data[0];
    }

    public int[] getV() {
        return data[1];
    }

    public int[] getW() {
        return data[2];
    }

    /*
     * res = this * vec 
     */
    public int[] multiV(int[] vec) {
        int[] tmp = new int[3];
        
        int i = 0;
        while(i < 3) {
            tmp[i] = data[i][0] * vec[0] + data[i][1] * vec[1] + data[i][2] * vec[2];
            i = i + 1;
        }
        return tmp;
    }

    public mat3x3 mutliM(mat3x3 mat) {
        int ux = data[0][0] * mat.data[0][0] + data[0][1] * mat.data[1][0] + data[0][2] * mat.data[2][0];
        int uy = data[0][0] * mat.data[0][1] + data[0][1] * mat.data[1][1] + data[0][2] * mat.data[2][1];
        int uz = data[0][0] * mat.data[0][2] + data[0][1] * mat.data[1][2] + data[0][2] * mat.data[2][2];

        int vx = data[1][0] * mat.data[0][0] + data[1][1] * mat.data[1][0] + data[1][2] * mat.data[2][0];
        int vy = data[1][0] * mat.data[0][1] + data[1][1] * mat.data[1][1] + data[1][2] * mat.data[2][1];
        int vz = data[1][0] * mat.data[0][2] + data[1][1] * mat.data[1][2] + data[1][2] * mat.data[2][2];

        int wx = data[2][0] * mat.data[0][0] + data[2][1] * mat.data[1][0] + data[2][2] * mat.data[2][0];
        int wy = data[2][0] * mat.data[0][1] + data[2][1] * mat.data[1][1] + data[2][2] * mat.data[2][1];
        int wz = data[2][0] * mat.data[0][2] + data[2][1] * mat.data[1][2] + data[2][2] * mat.data[2][2];

        return new mat3x3().init(ux, uy, uz, vx, vy, vz, wx, wy, wz);
    }

    public void print() {
        int x = 0;
        while (x < 3) {
            int y = 0;
            while(y < 3) {
                System.out.println(data[x][y]);
                y = y + 1;
            }
            x = x + 1;
        }
    }
}

class mat3vec3 {
    /*
     * datalayout:
     * 
     * u vec3
     * v vec3
     * w vec3
    */
    public vec3[] data;

    public mat3vec3 init(int ux, int uy, int uz, int vx, int vy, int vz, int wx, int wy, int wz) {
        data = new vec3[3];
        data[0] = new vec3().init(ux, uy, uz);
        data[1] = new vec3().init(vx, vy, vz);
        data[2] = new vec3().init(wx, wy, wz);
        return this;
    }


    public vec3 getX() {
        int ux = getU().getX();
        int vx = getV().getX();
        int wx = getW().getX();
        return new vec3().init(ux, vx, wx);
    }

    public vec3 getY() {
        int uy = getU().getY();
        int vy = getV().getY();
        int wy = getW().getY();
        return new vec3().init(uy, vy, wy);
    }

    public vec3 getZ() {
        int uz = getU().getZ();
        int vz = getV().getZ();
        int wz = getW().getZ();
        return new vec3().init(uz, vz, wz);
    }

    public vec3 getU() {
        return data[0];
    }

    public vec3 getV() {
        return data[1];
    }

    public vec3 getW() {
        return data[2];
    }

    /*
     * res = this * vec 
     */
    public vec3 multiV(vec3 vec) {
        return new vec3().init(
            getU().product(vec), 
            getV().product(vec), 
            getW().product(vec)
            );
    }

    public mat3vec3 mutliM(mat3vec3 mat) {
        vec3 x = getX();
        vec3 y = getY();
        vec3 z = getZ(); 
        return new mat3vec3().init(
            x.product(mat.getU()), x.product(mat.getV()), x.product(mat.getW()),
            y.product(mat.getU()), y.product(mat.getV()), y.product(mat.getW()),
            z.product(mat.getU()), z.product(mat.getV()), z.product(mat.getW())
            );
    }

    public void print() {
        getU().print();
        getV().print();
        getW().print();
    }
}

class Main {

    public vec3 e1;
    public vec3 e2;
    public vec3 e3;

    public mat3vec3 base;
    public mat3x3 baseraw;

    public static void main(String[] args) {
        Main main = new Main();
        main.init();
        main.printSeperatorTest(0);
        main.testVec();
        main.printSeperatorTest(1);
        main.testMat3x3();
        main.printSeperatorTest(2);
        main.testMatVec3();
    }
    
    public void init() {
        baseraw = new mat3x3().init(1, 0, 0, 0, 1, 0, 0, 0, 1);
        base = new mat3vec3().init(1, 0, 0, 0, 1, 0, 0, 0, 1);
        e1 = base.getU();
        e2 = base.getV();
        e3 = base.getW();
    }

    public void testVec() {
        if (e1.product(e2) == 0) {
            System.out.println(1);
        }

        if (e2.product(e3) == 0) {
            System.out.println(2);
        }

        if (e3.product(e1) == 0) {
            System.out.println(3);
        }

        if (e1.product(e1) == 1) {
            System.out.println(4);
        }

        if (e2.product(e2) == 1) {
            System.out.println(5);
        }

        if (e3.product(e3) == 1) {
            System.out.println(6);
        }
    }

    public void testMat3x3() {
        mat3x3 rotate = new mat3x3().init(0, 1, 0, 0, 0, 1, 1, 0, 0);
        vec3 values = new vec3().init(1, 2, 3);

        printIntArray(rotate.multiV(values.data));

        printSeperator(1);

        mat3x3 arising = new mat3x3().init(1, 2, 3, 4, 5, 6, 7, 8, 9);

        mat3x3 shift1 = rotate.mutliM(arising);
        mat3x3 shift2 = rotate.mutliM(shift1);
        mat3x3 shift3 = rotate.mutliM(shift2);

        arising.print();
        printSeperator(2);
        shift1.print();
        printSeperator(3);
        shift2.print();
        printSeperator(4);
        shift3.print();
        printSeperator(5);

        mat3x3 s12 = shift1.mutliM(shift2);
        mat3x3 s23 = shift2.mutliM(shift3);
        mat3x3 s31 = shift3.mutliM(shift1);
        s12.print();
        printSeperator(6);        
        s23.print();
        printSeperator(7);
        s31.print();
        printSeperator(8);        

        printIntArray(arising.multiV(values.data));
        printSeperator(9);        
        printIntArray(shift1.multiV(values.data));
        printSeperator(10);
        printIntArray(shift2.multiV(values.data));
        printSeperator(11);        
        printIntArray(shift3.multiV(values.data));
    }

    public void printIntArray(int[] array) {
        int i = 0;
        while (i < 3) {
            System.out.println(array[i]);
            i = i + 1;
        }
    }

    public void testMatVec3() {
        mat3vec3 rotate = new mat3vec3().init(0, 1, 0, 0, 0, 1, 1, 0, 0);
        vec3 values = new vec3().init(1, 2, 3);

        rotate.multiV(values).print();

        printSeperator(1);

        mat3vec3 arising = new mat3vec3().init(1, 2, 3, 4, 5, 6, 7, 8, 9);

        mat3vec3 shift1 = rotate.mutliM(arising);
        mat3vec3 shift2 = rotate.mutliM(shift1);
        mat3vec3 shift3 = rotate.mutliM(shift2);

        arising.print();
        printSeperator(2);
        shift1.print();
        printSeperator(3);
        shift2.print();
        printSeperator(4);
        shift3.print();
        printSeperator(5);

        mat3vec3 s12 = shift1.mutliM(shift2);
        mat3vec3 s23 = shift2.mutliM(shift3);
        mat3vec3 s31 = shift3.mutliM(shift1);
        s12.print();
        printSeperator(6);
        s23.print();
        printSeperator(7);
        s31.print();
        printSeperator(8);

        arising.multiV(values).print();
        printSeperator(9);
        shift1.multiV(values).print();
        printSeperator(10);
        shift2.multiV(values).print();
        printSeperator(11);
        shift3.multiV(values).print();
    }

    public void printSeperator(int i) {
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(32);
        System.out.write(83);
        System.out.write(101);
        System.out.write(112);
        System.out.write(58);
        System.out.write(32);
        System.out.println(i);
        System.out.println(i);
        System.out.flush();
    }

        public void printSeperatorTest(int i) {
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(45);
        System.out.write(32);

        System.out.write(84);
        System.out.write(101);
        System.out.write(115);
        System.out.write(116);
        System.out.write(32);
        System.out.println(i);
        System.out.flush();
    }
}
