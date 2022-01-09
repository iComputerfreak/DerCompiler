class Main {
	public static void main(String[] args) {
		Main m = new Main();
		System.out.println(m.bar());

		System.out.println(m.baz(1));
		System.out.println(m.baz(42));

		System.out.println(m.foo(0));
		System.out.println(m.foo(1));
		System.out.println(m.foo(2));
		System.out.println(m.foo(3));

		System.out.println(m.foo2(1));
		System.out.println(m.foo2(0));

		System.out.println(m.foo3(1));
		System.out.println(m.foo3(0));

		System.out.println(m.foo4(0, 0, 0));
		System.out.println(m.foo4(0, 0, 1));
		System.out.println(m.foo4(0, 1, 0));
		System.out.println(m.foo4(0, 1, 1));
		System.out.println(m.foo4(1, 0, 0));
		System.out.println(m.foo4(1, 0, 1));
		System.out.println(m.foo4(1, 1, 0));
		System.out.println(m.foo4(1, 1, 1));

		System.out.println(m.foo5(0, 0, 0));
		System.out.println(m.foo5(0, 0, 1));
		System.out.println(m.foo5(0, 1, 0));
		System.out.println(m.foo5(0, 1, 1));
		System.out.println(m.foo5(1, 0, 0));
		System.out.println(m.foo5(1, 0, 1));
		System.out.println(m.foo5(1, 1, 0));
		System.out.println(m.foo5(1, 1, 1));

		System.out.println(m.foo6(0));
		System.out.println(m.foo6(50));

		System.out.println(m.foo7(0));
		System.out.println(m.foo7(50));
	}

	public int bar() {
		int i = 0;
		while (i < 100) {

			if (i == 10) {
				return i;
			}

			i = i + 1;
		}

		return -1;
	}

	public int baz(int j) {
		int i = 42;
		
		if (i == j) {
			return 0;
		} else {
			return 1;
		}
	}

	public int foo(int i) {
		if (i == 0) {
		} else {
			if (i == 1) {
			} else {
				if (i == 2) {
				} else {
					return 3;
				}
			}
			return 4;
		}
		return 5;
	}

	public int foo2(int j) {
		if (j == 0) {
			while (j == 3) {
				if (j == 5) {
				} else {
					return 6;
				}
			}
		}
		return 7;
	}

	public int foo3(int j) {
		if (j == 0) {
			while (j == 3) {
				if (j == 5) {
					return 6;
				} else {
				}
			}
		}
		return 7;
	}

	public int foo4(int i, int j, int k) {
		if (i == 0) {
			if (j == 0) {
				if (k == 0) {
					System.out.println(1);
				} else {
					System.out.println(2);
				}
			} else {
				if (k == 0) {
					System.out.println(3);
				} else {
					System.out.println(4);
				}
			}
		} else {
			if (j == 0) {
				if (k == 0) {
					System.out.println(5);
				} else {
					System.out.println(6);
				}
			} else {
				if (k == 0) {
					System.out.println(7);
				} else {
					System.out.println(8);
				}
			}
		}
		return 1;
	}

	public int foo5(int i, int j, int k) {
		if (i == 0) {
			if (j == 0) {
				if (k == 0) {
				} else {
				}
			} else {
				if (k == 0) {
				} else {
					return 2;
				}
			}
			return 6;
		} else {
			if (j == 0) {
				if (k == 0) {
					return 3;
				} else {
				}
			} else {
				if (k == 0) {
				} else {
				}
			}
		}
		return 1;
	}

	public int foo6(int i) {
		while (i > 10) {
			if (i == 100) {
				return i;
			} else {

			}
			i = i + 1;
		}

		return 2;
	}

	public int foo7(int i) {
		while (i > 10) {
			if (i == 100) {
			} else {
				return i;
			}
		}

		return 2;
	}
}
