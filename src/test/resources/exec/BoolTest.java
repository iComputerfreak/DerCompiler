class BoolTest {

	/* This should never be called */
	public boolean error() {
		System.out.write(33); 
		System.out.write(69); 
		System.out.write(82);
		System.out.write(82);
		System.out.write(79);
		System.out.write(82);
		System.out.write(33); 
		return false;
	}

	public boolean sideEffect() {
		System.out.write(72);
		System.out.write(69);
		System.out.write(76);
		System.out.write(76);
		System.out.write(79);
		return true;
	}

	public boolean returnTest1(int x) {
		return (x % 2) == 0;
	}

	public boolean returnTestSC1(int x) {
		return (x % 2) == 0 && (x % 3) == 0;
	}

	public boolean returnTestSC2(int x) {
		return (x % 2) == 0 || (x % 3) == 0;
	}

	public boolean returnTestSC3(int x) {
		return (x % 2) == 0 && error();
	}

	public boolean returnTestSC4(int x) {
		return (x % 2) == 0 || error();
	}

	public int ifTest1(int x) {
		if (x % 2 == 0) {
			return x + 1;
		}
		return x + 2;
	}

	public int ifElseTest1(int x) {
		if (x % 2 == 0) {
			return x + 1;
		} else {
			return x + 2;
		}
	}

	public int ifElseTestStacked1(int x) {
		if (x % 2 == 0) {
			return x + 1;
		} else if (x % 3 == 0) {
			return x + 2;
		}
		return x + 3;
	}

	public int ifElseTestStacked2(int x) {
		if (x % 2 == 0 && x % 3 == 0) {
			return x + 1;
		} else if (x % 3 == 0) {
			return x + 2;
		}
		return x + 3;
	}

	public boolean foo1() {
		return true;
	}

	public boolean returnTest2() {
		return true && foo1();
	}

	public boolean foo2;

	public boolean returnTest3() {
		foo2 = true;
		return true && foo2;
	}

	public boolean returnTest4() {
		boolean[] foo3 = new boolean[1];
		foo3[0] = true;
		return true && foo3[0];
	}

	public boolean returnTest5() {
		boolean foo4 = true;
		return true && foo4;
	}

	public boolean returnTest6() {
		boolean foo5 = true;
		return foo5 && sideEffect();
	}

	public boolean returnTest7() {
		return true == true && sideEffect();
	}

	public boolean returnTest8() {
		return this != null && sideEffect();
	}

	public boolean returnTest9(int x) {
		return (x % 2) == 1 || sideEffect();
	}

	public boolean returnTest10(int x) {
		boolean b = ((false == true != true == false || false && false || false == false != true && true != false) || false != true == false == (true == false && false || true != false || (true || false != false && (true)) || false) && true == false && true || false != true) && false || !(true || false == (true && true == true != false && false == true && false) && false != true && true || true != true) != false || true == false == true != true == true;
		return b;
	}

	public boolean returnTest11() {
		if (true && (false || false == (false != false && false != false && true) && false) && true == true != false) {
			return true;
		} else {
			return false ;
		}
	}

	public boolean returnTest12(boolean b, int x) {
		boolean c = true;
		while (true && b && x > 2 && x < 10) {
			c = c && (x = x + 1) < 11 && x != 100;
		}
		return c;
	}

	public static void main(String[] args) {
		BoolTest test = new BoolTest();

		/* +++ Boolean in conditions +++ */
		System.out.write(test.ifTest1(65));
		System.out.write(test.ifTest1(66));

		System.out.write(test.ifElseTest1(67));
		System.out.write(test.ifElseTest1(68));

		System.out.write(test.ifElseTestStacked1(70));
		System.out.write(test.ifElseTestStacked1(72));
		System.out.write(test.ifElseTestStacked1(75));
		System.out.write(test.ifElseTestStacked1(77));

		System.out.write(test.ifElseTestStacked1(70));
		System.out.write(test.ifElseTestStacked1(72));
		System.out.write(test.ifElseTestStacked1(75));
		System.out.write(test.ifElseTestStacked1(77));
		
		/* +++ Boolean as expressions +++ */
		/* Simple return of boolean expression */
		if (test.returnTest1(3)) {
			System.out.write(65);
		} else {
			System.out.write(66);
		}

		/* Return Short-circuit, second evaluated */
		if (test.returnTestSC1(4)) {
			System.out.write(67);
		} else {
			System.out.write(68);
		}

		/* Return Short-circuit, second evaluated */
		if (test.returnTestSC2(3)) {
			System.out.write(67);
		} else {
			System.out.write(68);
		}

		/* Return Short-circuit, second not evaluated */
		if (test.returnTestSC3(5)) {
			System.out.write(67);
		} else {
			System.out.write(68);
		}

		/* Return Short-circuit, second not evaluated */
		if (test.returnTestSC4(4)) {
			System.out.write(69);
		} else {
			System.out.write(70);
		}

		/* Boolean expressions from different sources */
		if (test.returnTest2()) {
			System.out.write(71);
		} else {
			System.out.write(72);
		}

		if (test.returnTest3()) {
			System.out.write(73);
		} else {
			System.out.write(74);
		}

		if (test.returnTest4()) {
			System.out.write(75);
		} else {
			System.out.write(76);
		}

		if (test.returnTest5()) {
			System.out.write(77);
		} else {
			System.out.write(78);
		}

		if (test.returnTest6()) {
			System.out.write(79);
		} else {
			System.out.write(80);
		}

		if (test.returnTest7()) {
			System.out.write(80);
		} else {
			System.out.write(81);
		}

		if (test.returnTest8()) {
			System.out.write(82);
		} else {
			System.out.write(83);
		}

		if (test.returnTest9(4)) {
			System.out.write(84);
		} else {
			System.out.write(85);
		}

		if (test.returnTest10(4)) {
			System.out.write(86);
		} else {
			System.out.write(87);
		}

		if (test.returnTest11()) {
			System.out.write(88);
		} else {
			System.out.write(89);
		}

		if (test.returnTest12(true, 3)) {
			System.out.write(90);
		} else {
			System.out.write(91);
		}

		System.out.write(10);
	}

}
