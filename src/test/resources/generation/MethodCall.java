class Class {

	public int i;
	public int j;
	public int k;

	public static void main(String[] args) {
		Class c = new Class();
		c.k = c.init(4);
		System.out.println(c.j + c.i + c.k);
	}

	public int init(int i) {
		this.i = i;
		this.j = 5;
		return 6;
	}
}