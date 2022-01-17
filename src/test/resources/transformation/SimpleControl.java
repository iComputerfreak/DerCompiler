class foo {

    /*
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

    public void booleanCounter() {
        boolean b0 = false;
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;

        boolean run = true;

        while(run && (b3 = b2 && !(b2 = b1 && !(b1 = !(b0 && !(b0 = !b0))))) || (b2 = !(b1 && !(b1 = !(b0 && !(b0 = !b0))))) || (b1 = !(b0 && !(b0 = !b0)) || (b3 = !(b0 = !b0)) || run)) {
            if (b3) {
                run = false;
            }
        }
    }

    
    public boolean returnTest12(boolean b, int x) {
		boolean c = true;
		while (true && b && x > 2 && x < 10) {
			c = c && (x = x + 1) < 11 && x != 100;
		}
		return c;
	}
    */
        public boolean state_one() {
        boolean b = false == true != true == false || false && false || false == false != true && true != false;
        return b;
    }

    public boolean state_two() {
        boolean b = false != true == false == (true == false && false || true != false || (true || false != false && (true)) || false);
        return b;
    }

    public boolean state_three() {
        boolean b = true == false && true || false != true;
        return b;
    }

    public boolean state_four() {
        boolean b = !(true || false == (true && true == true != false && false == true && false) && false != true && true || true != true) != false;
        return b;
    }

    public boolean state_five() {
        boolean b = true == false == true != true == true;
		return b;
    }

    public static void main(String[] args) {
        foo main = new foo();
        if (main.state_one()) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        if (main.state_two()) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        if (main.state_three()) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        if (main.state_four()) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        if (main.state_five()) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        boolean b = 
        (
            main.state_one()
            || main.state_two()
            && main.state_three()
        )
        && false
        || main.state_four()
        || main.state_five();

        if (b) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
    }
}