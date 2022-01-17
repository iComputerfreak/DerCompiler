
class Main {
	public Main m;

	public static void main(String[] args) {
		String s = null;

		if (s == null) {
			System.out.println(1);
		}

		Main m = null;

		if (m == null) {
			System.out.println(2);
		}

		m = new Main();

		if (m == null) {
			System.out.println(3);
		} else {
			System.out.println(4);
		}

		if (m.m == null) {
			System.out.println(5);
		}

		m.bar();

		if (m.baz() == null) {
			System.out.println(12);
		}

		if (m.baz1() == null) {
			System.out.println(13);
		}

		int[][] i = null;
		
		if (i == null) {
			System.out.println(14);
		}

		i = new int[12][];

		if (i == null) {
			System.out.println(15);
		}

		if (i[0] == null) {
			System.out.println(16);
		}

		i[0] = new int[1];

		if (i[0] == null) {
			System.out.println(17);
		}
	}

	public void bar() {
		if (m == null) {
			System.out.println(6);
		}

		if (this == null) {
			System.out.println(7);
		}

		foo(null, 8);

		foo(this, 9);

		foo(this.m, 10);

		this.m = null;

		foo(this.m, 11);
	}

	public void foo(Main m, int i) {
		if (m == null) {
			System.out.println(i);
		}
	}

	public Main baz() {
		return null;
	}

	public Main baz1() {
		return new Main();
	}
}
