class Main {
	public static void main(String[] args) {
		ArrayList list = new ArrayList();
		list.init();

		int i = 0;
		while (i < 1000) {
			list.add(i);
			i = i + 1;
		}

		list.print();

		System.out.println(list.sum());
	}
}

class ArrayList {
	public int length;
	public int capacity;
	public int[] data;

	public void init() {
		this.length = 0;
		this.capacity = 0;
	}

	public void add(int i) {
		if (this.capacity == this.length) {
			increaseCapacity();
		}

		this.data[this.length] = i;
		this.length = this.length + 1;
	}

	public void increaseCapacity() {
		int newCap = 2 * this.capacity;
		if (newCap == 0) {
			newCap = 4;
		}

		int[] newData = new int[newCap];
		
		int idx = 0;
		while (idx < this.length) {
			newData[idx] = this.data[idx];

			idx = idx + 1;
		}

		this.data = newData;
		this.capacity = newCap;
	}

	public void print() {
		int idx = 0;
		while (idx < this.length) {
			System.out.println(this.data[idx]);
			idx = idx + 1;
		}
	}

	public int sum() {
		int sum = 0;
		int idx = 0;
		while (idx < this.length) {
			sum = sum + this.data[idx];
			idx = idx + 1;
		}
		return sum;
	}
}
