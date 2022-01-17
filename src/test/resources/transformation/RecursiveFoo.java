class Foo{

	public Foo foo;

	public static void main(String[] args){
		
		Foo foo = new Foo();
		
		foo.foo = foo;
		
		foo.foo(1024);
	
	}
	
	public void foo(int i) {
		if (i > 12) {
			foo.foo.foo(i / 2);
			System.out.println(i);
		}
	}
	
}
