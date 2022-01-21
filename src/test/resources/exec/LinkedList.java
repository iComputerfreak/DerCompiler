class Main {
	public static void main(String[] args) {
		LinkedList list = new LinkedList();
		list.init();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);

		list.print();

		list.remove(2);
		list.remove(3);

		list.print();

		list.add(100);
		list.add(-1);

		list.print();
	}
}

class Node {
	public int data;
	public Node next;
}

class LinkedList {
	public Node start;

	public void init() {
		this.start = null;
	}

	public void add(int i) {
		if (this.start == null) {
			this.start = new Node();
			this.start.data = i;
			return;
		}

		Node current = this.start;

		while (current.next != null) {
			current = current.next;
		}

		current.next = new Node();
		current.next.data = i;
	}

	public void append(LinkedList other) {
		Node current = this.start;

		while (current.next != null) {
			current = current.next;
		}

		current.next = other.start;
	}

	public void remove(int i) {
		if (this.start.data == i) {
			this.start = this.start.next;
			return;
		}

		Node current = this.start;
		while (current.next != null) {
			if (current.next.data == i) {
				current.next = current.next.next;
			}

			current = current.next;
		}
	}

	public void print() {
		Node current = this.start;

		while (current != null) {
			System.out.println(current.data);
			current = current.next;
		}
	}
}

