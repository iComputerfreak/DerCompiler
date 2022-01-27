class Data {
    
    public int res;
    public int i;

    public Data init(int res, int i) {
        this.res = res;
        this.i = i;
        return this;
    }

    public int foo() {
        return res / i;
    }
}

class DataArray {
    public Data[][] data;
    public int length;
    public int width;

    public DataArray init(int width) {
        this.width = width;
        data = new Data[width][];
        return this;
    }
    public DataArray init2(int length) {
        this.length = length;
        int i = 0;
        int x = 0;
        while (x < width) {
            int y = 0;
            data[x] = new Data[length];
            while (y < length) {
                data[x][y] = new Data().init(x * x * y, (i = i + 1));
                y = y + 1;
            }
            x = x + 1;
        }
        return this;
    }
}


class Main {
    
    public static void main(String[] args) {
        Main main = new Main();
        main.test5_5();
        main.test5_10();
        main.test10_10();
        main.testArising();
    }

    public void test5_5() {
        test(5, 5);
    }

    public void test5_10() {
        test(5, 10);
    }

    public void test10_10() {
        test(10, 10);
    }

    public void testArising() {
        int i = 1;
        while (i < 20) {
            test(21 - i, i);
            i = i + 1;
        }
    }

    public void test(int width, int height) {
        DataArray data = new DataArray().init(width).init2(height);
        int sum = 0;
        int x = 0;

        while (x < data.width) {
            int y = 0;
            while (y < data.length) {
                sum = sum + data.data[x][y].foo();
                y = y + 1;
            }
            x = x + 1;
        }
        
        System.out.println(sum);
    }
}