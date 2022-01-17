class infiniteLoop {
    public static void main(String[] args) {
        int counter = 48;
        while (true && !false) {
            System.out.write(counter);
	    System.out.flush();
            counter = counter + 1;
            if (counter > 57)
                return;
        }
    }
}
