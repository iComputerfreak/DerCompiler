/* When a System type is present, System.in refers to a static field within System
* (which does not exist in Mini-Java), and should hence fail instead of succeed
* like if the builtin method was used instead. */

class System {

}

class Main {
	public static void main(String[] args) {
		int x = System.in.read();
	}
}
