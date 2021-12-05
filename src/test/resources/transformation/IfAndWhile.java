class Main {
	
	public int a;
	public boolean b;
	
	public static void main(String[] args) {
		Main m = new Main();
		
		System.out.println(0);
		m.a = 42;
		m.b = true;
		if ((m.a == m.f()) == m.b) {
			System.out.println(1);
		}
		System.out.println(m.f());
		while (m.b) {
			System.out.println(2);
			m.b = false;
		}
		System.out.println(3);
	}
	
	public int f() {
		return 42;
	}
}
