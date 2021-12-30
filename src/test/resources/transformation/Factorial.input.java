class Factorial {
    public static void main(String[] args) throws Exception {
        Calc c = new Calc();
        c.reset();
        while (true) {
            int result = c.getInput();
            if (result == -1) {
            System.out.println(-1);
            return;
	    }
	    System.out.println(result);
	    c = new Calc();
            c.reset();
        }
    }
}

class Calc {
    public int input;
    public int number;

    public void readNext() throws Exception {
        input = System.in.read();
    }

    public void reset() {
        number = 0;
    }

    public int getInput() throws Exception {
        readNext();
        if (input == 10) readNext();
        while (isDigit(input)) {
            number = 10 * number;
            number = number + (input - 48);
            readNext();
        }
        if (input == 33) {
            return fact(number);
        } else {
	    return -1;
        }
    }

    public int fact(int factNum) {
        if (factNum > 0) {
            return fact(factNum - 1) * factNum;
        }
        return 1;
    }

    public boolean isDigit(int digit) {
        return 48 <= digit && digit < 58;
    }


}
