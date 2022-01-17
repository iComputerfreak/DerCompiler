class CorrectStepping {
    public static void main(String[] args) {
        System.out.write(48);
	System.out.flush();
        Steps step = new Steps();
        if (true && (false || 48 < 356)) {
            System.out.write(49);
	    System.out.flush();
        } else {
            System.out.write(48);
	    System.out.flush();
        }
        System.out.write(step.step1());
	System.out.flush();
        if (step.step2() < 54 || step.noStep() == 4) {
            System.out.write(step.step2());
	    System.out.flush();
        } else {
            System.out.write(step.noStep());
	    System.out.flush();
        }

    }

}

class Steps {

    public int step1() {
        System.out.write(50);
	System.out.flush();
        return 51;
        return 52;
    }

    public int step2() {
        int part1 = 5;
        int part2 = 2;
        return part2 + 10 * part1;
    }

    public int noStep() {
        System.out.write(48);
	System.out.flush();
        return 5 * 10 / 2 + 35;
    }
}
