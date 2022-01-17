class deadCode {
    public static void main(String[] args) {
        Death death = new Death();
        death.deadAfterReturn();
        death.lazyEvalDeath();
    }
}

class Death {
    public int[] array;

    public void deadAfterReturn() {
        int a = 3;
        int b = 5;
        boolean c = false;
        b = a + b;
        a = b * a;
        a = a * a + b;
        c = !c;
	System.out.write(48);
	System.out.flush();
        return;
        System.out.write(50);
	System.out.flush();
        array[10] = 5;
        b = a * b * 0;
        a = a / b;
    }

    public boolean lazyEvalDeath() {
        boolean boo = true;
        boolean test = false;
        int a = 46;
        int b = 2;
        int c = a / b;
        int d = 0;
	System.out.write(49);
	System.out.flush();
        return (test || a == b || boo && test != boo || lazyEvalDeath()) && (test || boo || a / d < 5);
    }

    public boolean infinite;

    public boolean neverUsedDeath(boolean infinite) {
        System.out.write(48);
        boolean infi = false;
        boolean nite = true;
        if (infinite) {
            infinite = !this.infinite;
        }
        this.infinite = infinite;
        infinite = infi || nite;
        return neverUsedDeath(infinite);

    }
}
