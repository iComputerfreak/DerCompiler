class Ackermann
{
	public int test(int i){
		int x = 1;
		while (true){
			x = 2 -x;
		}
		return x;
	}

    public static void main(String[] args)
    {
		Ackermann a = new Ackermann();
		
        int x = a.test(1);
		int y = x + 1;
    }
}