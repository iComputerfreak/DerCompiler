class foo {

    public static void main(String[] args) {
        int i = 0;
        if (5 == 6) {
            i = 3;
        }
        
        if (3 == 3) {
            while(true) {
                i = i + 2;
            }
        }
        
        while(i != 0) {
            i = i + 2;
        }
    }

    public void mainDummy() {
        int i = 0;
        if (5 == 6) {
            i = 3;
        }
        
        if (3 == 3) {
            while(true) {
                i = i + 2;
            }
        }
        
        while(i != 0) {
            i = i + 2;
        }
        
        while(i >= 0) {
            if (i > 20) {
                i = -10;
            }
            {
                i = i + 1;
            }
        }
        
    }

    public int bar() {
        int a = 2;
        return a;
    }

    public int simpleWhile() {
        int i = 10;
        while(i > 0) {
            i = i - (i + 2) / 2;
        }
        return i;
    }

    public int calc() {
        int a = 4 + 3 - 2 * 8;
        return a;
    }

    public boolean bool() {
        boolean b = 5 == 4;
        return b;
    }
    
    public int lazyAndOr() {
        int x = 3;

        if (3 == 7 && (4 < 5 || true)) {
            x = 4 + x;
        } else {
            x = 3;
        }
        return x;
    }

    public int simpleIf() {
        int x = 0;
        if (7 > 4) {
            x = 9;
        } else {
            x = 3;
        }
        return x;
    }

    public int 
}