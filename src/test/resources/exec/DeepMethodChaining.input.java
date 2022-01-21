class Main {

	public int x;
	public int y;

	public static void main(String[] args) {
		Main m = new Main();
		m = m.init()
			.addToX(10)
			.addToY(10)
			.print()
			.addToX(3)
			.addToX(5)
			.print()
			.bar()
			.bar()
			.bar()
			.bar()
			.bar()
			.bar()
			.addToY(-4)
			.print()
			.newMain()
			.addToY(4)
			.addToY(4)
			.addToY(4)
			.addToY(4 + m.y)
			.addToX(4)
			.addToY(14)
			.addToY(-5)
			.print()
			.init()
			.print();

		int sum = m.x + m.y;
		System.out.println(sum);
	}

	public Main init() {
		this.x = 1;
		this.y = 1;
		return this;
	}

	public Main bar() {
		return this;
	}

	public Main addToX(int i) {
		this.x = x + i;
		return this;
	}

	public Main addToY(int i) {
		this.y = y + i;
		return this;
	}

	public Main print() {
		System.out.println(this.x);
		System.out.println(this.y);
		return this;
	}

	public Main newMain() {
		return new Main();
	}
}
