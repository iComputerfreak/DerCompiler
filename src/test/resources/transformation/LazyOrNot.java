class Foo{

	public int i;

	public static void main(String[] args){
		
		Foo test = new Foo();
		test.i = 1;
		
		if (true || test.increment()) {
			System.out.println(13);
		}
		
		System.out.println(test.i);
		
		if (test.increment() || test.increment()) {
			System.out.println(test.i);
		}
	
	}
	
	public boolean increment() {
		i = i + 1;
		return true;
	}
	
}
