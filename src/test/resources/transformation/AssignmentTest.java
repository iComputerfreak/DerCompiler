class Main {
	public int i;
	public Main m;
	public Main n;
	public Main[] many;

	public static void main(String[] args) {
		Main m = new Main();
		m.doSomething();
	}

	public void doSomething() {
		m = new Main();
		n = new Main();
		m.m = new Main();
		many = new Main[100];

		int i = 0;
		while (i < 100) {
			many[i] = new Main();
			many[i].i = i;

			many[i].m = new Main();

			i = i + 1;
		}

		m.i = many[many[9].i = many[10].i = many[11].i].i;
		
		if (12 == (m.i = m.m.i))
			System.out.println(1);

		if (true && 13 == (i = 13)) {
		}
		System.out.println(i);

		if (true && 13 == (i = 14)) {
		}
		System.out.println(i);

		i = 0;
		while (i < 100) {
			System.out.println(many[i].i);

			i = i + 1;
		}
	}
}
