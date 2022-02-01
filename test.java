class a {
	public a[] array;
	public int n;
	public static void main(String[] args) {
		new a().q();
	}
	
	public void q() {
		a v = new a();
		v.n = 3;
		array = new a[1];
		array[0] = v;
		System.out.println(array[0].n);
	}
}