class Main {
	
	public static void main(String[] args) {
		Main m = new Main();
		m.foo();
	}
	
	public void foo() {
		int[][][] array = new int[100][][];
		int i = 0;
		while (i < 100) {
			array[i] = new int[100][];
			int j = 0;
			while (j < 100) {
				array[i][j] = new int[100];
				int k = 0;
				while (k < 100) {
					array[i][j][k] = i + j + k;
					k = k + 1;
				}
				j = j + 1;
			}
			i = i + 1;
		}
		
		int[][] arrayRed = new int[100][];
		int l = 0;
		while (l < 100) {
			arrayRed[l] = new int[100];
			int m = 0;
			while (m < 100) {
				arrayRed[l][m] = sum(array[l][m], 100);
				m = m + 1;
			}
			l = l + 1;
		}
		
		int[] arrayRed2 = new int[100];
		int n = 0;
		while (n < 100) {
			arrayRed2[n] = sum(arrayRed[n], 100);
			n = n + 1;
		}
		
		System.out.println(sum(arrayRed2, 100));
	}
	
	public int sum(int[] array, int length) {
		int i = 0;
		int sum = 0;
		while (i < length) {
			sum = sum + array[i];
			i = i + 1;
		}
		return sum;
	}
}