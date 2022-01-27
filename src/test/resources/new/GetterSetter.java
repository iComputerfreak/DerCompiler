class Main {
    public static void main(String[] args) {
        GetterSetterTest test = new GetterSetterTest().init();
        test.testSimple();
        test.testSetter();
        test.testGetter();
        test.testGetterSetter();
    }
}

class Data {
    public Data[] datastore;
    public Data data;
    public int[] array;
    public int value;
    public boolean[] status;
    public boolean b;

    public Data init() {
        datastore = new Data[value];
        data = new Data();
        array = new int[value];
        status = new boolean[value];
        return this;
    }

    public Data initValue(int v) {
        value = v;
        return init();
    }

    public Data[] getDatastore() {
        return datastore;
    }

    public void setDatastore(Data[] value) {
        datastore = value;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data value) {
        data = value;
    }

    public void setArray(int[] value) {
        array = value;
    }

    public int[] getArray() {
        return array;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean[] getStatus() {
        return status;
    }

    public void setStatus(boolean[] value) {
        status = value;
    }

    public boolean getBoolean() {
        return b;
    }

    public void setBoolean(boolean value) {
        b = value;
    }
}

class GetterSetterTest {

    public Equal eq;

    public GetterSetterTest init() {
        eq = new Equal();
        return this;
    }

    public void testSimple() {
        Data data = new Data();
        data.datastore = new Data[10];
        data.data = new Data();
        data.array = new int[10];
        data.value = 10;
        data.status = new boolean[10];
        data.b = true;

        /* check references */
        Data test = new Data();
        if (!eq.equalData(test, data)) {
            System.out.println(0);
        }
        if (!eq.equalData(data, test)) {
            System.out.println(1);
        }
        test.initValue(10);
        test.b = true;
        if (eq.equalData(data, test)) {
            System.out.println(2);
        }
        test.datastore = data.datastore;
        if (eq.equalData(data, test)) {
            System.out.println(3);
        }
        test.data = data.data;
        if (eq.equalData(data, test)) {
            System.out.println(4);
        }
        test.array = data.array;
        if (eq.equalData(data, test)) {
            System.out.println(5);
        }
        test.status = data.status;
        if (eq.equalData(data, test)) {
            System.out.println(6);
        }
        /* check values inside structure */
        data.array[3] = 5;
        test.initValue(20);
        test.value = 10;
        
        if (!eq.equalData(data, test)) {
            System.out.println(7);
        }
        test.array[3] = 5;
        if (eq.equalData(data, test)) {
            System.out.println(8);
        }
        
        data.datastore[5] = new Data();
        if (!eq.equalData(data, test)) {
            System.out.println(9);
        }
        test.datastore = new Data[test.value];
        if (!eq.equalData(data, test)) {
            System.out.println(10);
        }
        test.datastore[5] = data.datastore[5];
        if (eq.equalData(data, test)) {
            System.out.println(11);
        }

        test.status = data.status;
        test.status[1] = true;
        test.status[2] = true;
        test.status = new boolean[test.value];
        if (!eq.equalData(data, test)) {
            System.out.println(12);
        }
        
        test.status[1] = true;
        test.status[2] = true;
        if (eq.equalData(data, test)) {
            System.out.println(13);
        }

        test.data = data;
        data.data = test;
        if (eq.equalData(data, data.data.data)) {
            System.out.println(14);
        }
        if (eq.equalData(test, test.data.data)) {
            System.out.println(15);
        }
        int i = 16;
        Data tmp = test;
        /*this can produce a endlosloop when compiler is broken */
        while (i < 25) {
            if (i % 2 == 0) {
                if (eq.equalData(tmp, test)) {
                    System.out.println(i);
                }
            } else {
                if (eq.equalData(tmp, data)) {
                    System.out.println(i);
                }
            }
            tmp = tmp.data;
            i = i + 1;
        }
    }
 
    public void testSetter() {
        Data data = new Data();
        data.datastore = new Data[10];
        data.data = new Data();
        data.array = new int[10];
        data.value = 10;
        data.status = new boolean[10];
        data.b = true;

        /* check references */
        Data test = new Data();
        if (!eq.equalData(test, data)) {
            System.out.println(25);
        }
        if (!eq.equalData(data, test)) {
            System.out.println(26);
        }
        test.initValue(10);
        test.setBoolean(true);
        if (eq.equalData(data, test)) {
            System.out.println(27);
        }
        test.setDatastore(data.datastore);
        if (eq.equalData(data, test)) {
            System.out.println(28);
        }
        test.setData(data.data);
        if (eq.equalData(data, test)) {
            System.out.println(29);
        }
        test.setArray(data.array);
        if (eq.equalData(data, test)) {
            System.out.println(30);
        }
        test.setStatus(data.status);
        if (eq.equalData(data, test)) {
            System.out.println(31);
        }
        /* check values inside structure */
        data.array[3] = 5;
        test.initValue(20);
        test.setValue(10);
        
        if (!eq.equalData(data, test)) {
            System.out.println(32);
        }
        test.array[3] = 5;
        if (eq.equalData(data, test)) {
            System.out.println(33);
        }
        
        data.datastore[5] = new Data();
        if (!eq.equalData(data, test)) {
            System.out.println(34);
        }
        test.setDatastore(new Data[test.value]);
        if (!eq.equalData(data, test)) {
            System.out.println(35);
        }
        test.datastore[5] = data.datastore[5];
        if (eq.equalData(data, test)) {
            System.out.println(36);
        }

        test.status = data.status;
        test.status[1] = true;
        test.status[2] = true;
        test.setStatus(new boolean[test.value]);
        if (!eq.equalData(data, test)) {
            System.out.println(37);
        }
        
        test.status[1] = true;
        test.status[2] = true;
        if (eq.equalData(data, test)) {
            System.out.println(38);
        }

        test.setData(data);
        data.setData(test);
        if (eq.equalData(data, data.data.data)) {
            System.out.println(39);
        }
        if (eq.equalData(test, test.data.data)) {
            System.out.println(40);
        }
        int i = 41;
        Data tmp = test;
        /*this can produce a endlosloop when compiler is broken*/
        while (i < 50) {
            if (i % 2 != 0) {
                if (eq.equalData(tmp, test)) {
                    System.out.println(i);
                }
            } else {
                if (eq.equalData(tmp, data)) {
                    System.out.println(i);
                }
            }
            tmp = tmp.data;
            i = i + 1;
        }
    }

    public void testGetter() {
        Data data = new Data();
        data.datastore = new Data[10];
        data.data = new Data();
        data.array = new int[10];
        data.value = 10;
        data.status = new boolean[10];
        data.b = true;

        /* check references */
        Data test = new Data();
        if (!eq.equalData(test, data)) {
            System.out.println(50);
        }
        if (!eq.equalData(data, test)) {
            System.out.println(51);
        }
        test.initValue(10);
        test.b = true;
        if (eq.equalData(data, test)) {
            System.out.println(52);
        }
        test.datastore = data.datastore;
        if (eq.equalData(data, test)) {
            System.out.println(53);
        }
        test.data = data.getData();
        if (eq.equalData(data, test)) {
            System.out.println(54);
        }
        test.array = data.getArray();
        if (eq.equalData(data, test)) {
            System.out.println(55);
        }
        test.status = data.getStatus();
        if (eq.equalData(data, test)) {
            System.out.println(56);
        }
        /* check values inside structure */
        data.getArray()[3] = 5;
        test.initValue(20);
        test.value = 10;
        
        if (!eq.equalData(data, test)) {
            System.out.println(57);
        }
        test.getArray()[3] = 5;
        if (eq.equalData(data, test)) {
            System.out.println(58);
        }
        
        data.getDatastore()[5] = new Data();
        if (!eq.equalData(data, test)) {
            System.out.println(59);
        }
        test.datastore = new Data[test.getValue()];
        if (!eq.equalData(data, test)) {
            System.out.println(60);
        }
        test.getDatastore()[5] = data.getDatastore()[5];
        if (eq.equalData(data, test)) {
            System.out.println(61);
        }

        test.status = data.status;
        test.status[1] = true;
        test.status[2] = true;
        test.status = new boolean[test.getValue()];
        if (!eq.equalData(data, test)) {
            System.out.println(62);
        }
        
        test.status[1] = true;
        test.status[2] = true;
        if (eq.equalData(data, test)) {
            System.out.println(63);
        }

        test.data = data;
        data.data = test;
        if (eq.equalData(data, data.data.data)) {
            System.out.println(64);
        }
        if (eq.equalData(test, test.data.data)) {
            System.out.println(65);
        }
        int i = 66;
        Data tmp = test;
        /*this can produce a endlosloop when compiler is broken*/
        while (i < 75) {
            if (i % 2 == 0) {
                if (eq.equalData(tmp, test)) {
                    System.out.println(i);
                }
            } else {
                if (eq.equalData(tmp, data)) {
                    System.out.println(i);
                }
            }
            tmp = tmp.getData();
            i = i + 1;
        }
    }

    public void testGetterSetter() {
        Data data = new Data();
        data.datastore = new Data[10];
        data.data = new Data();
        data.array = new int[10];
        data.value = 10;
        data.status = new boolean[10];
        data.b = true;

        /* check references */
        Data test = new Data();
        if (!eq.equalData(test, data)) {
            System.out.println(75);
        }
        if (!eq.equalData(data, test)) {
            System.out.println(76);
        }
        test.initValue(10);
        test.setBoolean(true);
        if (eq.equalData(data, test)) {
            System.out.println(77);
        }
        test.setDatastore(data.getDatastore());
        if (eq.equalData(data, test)) {
            System.out.println(78);
        }
        test.setData(data.getData());
        if (eq.equalData(data, test)) {
            System.out.println(79);
        }
        test.setArray(data.getArray());
        if (eq.equalData(data, test)) {
            System.out.println(80);
        }
        test.setStatus(data.getStatus());
        if (eq.equalData(data, test)) {
            System.out.println(81);
        }
        /* check values inside structure */
        data.getArray()[3] = 5;
        test.initValue(20);
        test.setValue(10);
        
        if (!eq.equalData(data, test)) {
            System.out.println(82);
        }
        test.getArray()[3] = 5;
        if (eq.equalData(data, test)) {
            System.out.println(83);
        }
        
        data.getDatastore()[5] = new Data();
        if (!eq.equalData(data, test)) {
            System.out.println(84);
        }
        test.setDatastore(new Data[test.getValue()]);
        if (!eq.equalData(data, test)) {
            System.out.println(85);
        }
        test.getDatastore()[5] = data.getDatastore()[5];
        if (eq.equalData(data, test)) {
            System.out.println(86);
        }

        test.status = data.status;
        test.getStatus()[1] = true;
        test.getStatus()[2] = true;
        test.setStatus(new boolean[test.getValue()]);
        if (!eq.equalData(data, test)) {
            System.out.println(87);
        }
        
        test.getStatus()[1] = true;
        test.getStatus()[2] = true;
        if (eq.equalData(data, test)) {
            System.out.println(88);
        }

        test.setData(data);
        data.setData(test);
        if (eq.equalData(data, data.data.data)) {
            System.out.println(89);
        }
        if (eq.equalData(test, test.data.data)) {
            System.out.println(90);
        }
        int i = 91;
        Data tmp = test;
        /*this can produce a endlosloop when compiler is broken*/
        while (i < 100) {
            if (i % 2 != 0) {
                if (eq.equalData(tmp, test)) {
                    System.out.println(i);
                }
            } else {
                if (eq.equalData(tmp, data)) {
                    System.out.println(i);
                }
            }
            tmp = tmp.getData();
            i = i + 1;
        }
    }
}

class Equal {

    public boolean equalData(Data lhs, Data rhs) {
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null) {
            return false;
        }
        if (rhs == null) {
            return false;
        }
        if (lhs.value == rhs.value 
            && lhs.b == rhs.b
            && equalData(lhs.data, rhs.data)
            && equalDataStore(lhs.datastore, rhs.datastore, lhs.value) 
            && equalStatus(lhs.status, rhs.status, lhs.value)
            && equalArray(lhs.array, rhs.array, lhs.value)) {
            return true;
        }
        return false;
    }

    public boolean equalDataStore(Data[] lhs, Data[] rhs, int length) {
        if (lhs == rhs) {
            return true;
        }
        int i = 0;
        while (i < length) {
            if (!equalData(lhs[i], rhs[i])) {
                return false;
            }
            i = i + 1;
        }
        return true;
    }

    public boolean equalStatus(boolean[] lhs, boolean[] rhs, int length) {
        if (lhs == rhs) {
            return true;
        }
        int i = 0;
        while (i < length) {
            if (lhs[i] != rhs[i]) {
                return false;
            }
            i = i + 1;
        }
        return true;
    }

    public boolean equalArray(int[] lhs, int[] rhs, int length) {
        if (lhs == rhs) {
            return true;
        }
        int i = 0;
        while (i < length) {
            if (lhs[i] != rhs[i]) {
                return false;
            }
            i = i + 1;
        }
        return true;
    }
}