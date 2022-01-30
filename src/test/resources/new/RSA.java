class Main {

	public int p;
	public int q;
	public int n;
	public int lambda;
	public int e;
	public int d;
	public Operations op;
	public int lfsr;
	public int randMax;
	
	public static void main(String[] args) {
		Main m = new Main();
		m.init();
		m.run();
	}
	
	public void init() {
		/* Init with seed */
		lfsr = 26716;
		randMax = 65517;
	}
	
	public void run() {
		op = new Operations();
		op.init();
		init();
		keygen();
		/* Sadly the algorithm is not correct due to integer overflows, but the results are still deterministic, due to the PRNG */
		System.out.println(encrypt(5028));
        System.out.println(encrypt(8527));
        System.out.println(encrypt(6024));
        System.out.println(encrypt(2808));
        System.out.println(encrypt(861));
        System.out.println(encrypt(9126));
        System.out.println(encrypt(9292));
        System.out.println(encrypt(5851));
        System.out.println(encrypt(5943));
        System.out.println(encrypt(4414));
        System.out.println(encrypt(9670));
        System.out.println(encrypt(326));
        System.out.println(encrypt(164));
        System.out.println(encrypt(1942));
        System.out.println(encrypt(760));
        System.out.println(encrypt(8515));
        System.out.println(encrypt(7827));
        System.out.println(encrypt(7717));
        System.out.println(encrypt(7194));
        System.out.println(encrypt(3792));
        System.out.println(encrypt(6820));
        System.out.println(encrypt(9828));
        System.out.println(encrypt(9759));
        System.out.println(encrypt(1568));
        System.out.println(encrypt(5241));
        System.out.println(encrypt(2585));
        System.out.println(encrypt(5616));
        System.out.println(encrypt(632));
        System.out.println(encrypt(6842));
        System.out.println(encrypt(3802));
        System.out.println(encrypt(461));
        System.out.println(encrypt(5821));
        System.out.println(encrypt(650));
        System.out.println(encrypt(6641));
        System.out.println(encrypt(7078));
        System.out.println(encrypt(3934));
        System.out.println(encrypt(584));
        System.out.println(encrypt(5341));
        System.out.println(encrypt(9596));
        System.out.println(encrypt(6836));
        System.out.println(encrypt(9679));
        System.out.println(encrypt(6686));
        System.out.println(encrypt(9985));
        System.out.println(encrypt(270));
        System.out.println(encrypt(7497));
        System.out.println(encrypt(6376));
        System.out.println(encrypt(8439));
        System.out.println(encrypt(9148));
        System.out.println(encrypt(2060));
        System.out.println(encrypt(9938));
        System.out.println(encrypt(54));
        System.out.println(encrypt(9903));
        System.out.println(encrypt(11));
        System.out.println(encrypt(1411));
        System.out.println(encrypt(2855));
        System.out.println(encrypt(6117));
        System.out.println(encrypt(402));
        System.out.println(encrypt(4557));
        System.out.println(encrypt(802));
        System.out.println(encrypt(6316));
        System.out.println(encrypt(3353));
        System.out.println(encrypt(9512));
        System.out.println(encrypt(3345));
        System.out.println(encrypt(3419));
	}
	
	public int random() {
		/* https://en.wikipedia.org/wiki/Linear-feedback_shift_register */
		int bit  = op.and_i(( op.xor_i(op.xor_i(op.xor_i(op.shr_i(lfsr, 0), op.shr_i(lfsr, 2)), op.shr_i(lfsr, 3)), op.shr_i(lfsr, 5)) ), 1);
		return lfsr = op.or_i((op.shr_i(lfsr, 1)), (op.shl_i(bit, 15)));
	}
	
	/* Returns random number in [min, max) */
	public int randomNumber(int min, int max) {
		return min + random() * max / randMax;
	}
	
	public void keygen() {
		/* Choose two distinct prime numbers < 500 */
		p = randomNumber(2, 500);
		while (!isPrime(p)) {
			p = randomNumber(0, 500);
		}
		q = randomNumber(2, 500);
		while (!isPrime(q)) {
			q = randomNumber(0, 500);
		}
		n = p * q;
		lambda = lcm(p - 1, q - 1);
		
		boolean found = false;
        e = 3;
        while (e < lambda && !found) {
            if (gcd(e, lambda) == 1) {
                d = 1;
                /* Find d with d * e = 1 mod lambda */
                /* Search until we reach integer overflow and d becomes negative */
                while (d * e % lambda != 1 && d > 0) {
                    d = d + 1;
                }
                if (d * e % lambda == 1) {
                    found = true;
                }
            }
            if (!found) {
                e = e + 1;
            }
        }
	}
	
	public int lcm(int a, int b) {
		return abs(a * b) / gcd(a, b);
	}
	
	/* Euclidean algorithm */
	public int gcd(int a, int b) {
		int x = a;
		int y = b;
		while (y != 0) {
			int t = y;
			y = x % y;
			x = t;
		}
		return x;
	}
	
	public int abs(int a) {
		if (a < 0) {
			return -a;
		}
		return a;
	}
	
	public boolean isPrime(int n) {
		if (n <= 1) {
			return false;
		}
		if (n <= 3) {
			return true;
		}
		
		if (n % 2 == 0 || n % 3 == 0) {
			return false;
		}
		
		int i = 5;
		while (i * i <= n) {
			if (n % i == 0 || n % (i + 2) == 0) {
				return false;
			}
			i = i + 6;
		}
		
		return true;
	}
	
	public int encrypt(int value) {
		/* PK: (n, e) */
		return op.pow(value, e) % n;
	}
	
	public int decrypt(int cipher) {
		/* SK: (n, e, d) */
		return op.pow(cipher, d) % n;
	}
}

/* Shamelessly stolen from Base64.input.java */

class Operations {

	public int[] powers;

	public void init() {
		powers = new int[32];
		powers[0] = 1;
		powers[1] = 2;
		powers[2] = 4;
		powers[3] = 8;
		powers[4] = 16;
		powers[5] = 32;
		powers[6] = 64;
		powers[7] = 128;
		powers[8] = 256;
		powers[9] = 512;
		powers[10] = 1024;
		powers[11] = 2048;
		powers[12] = 4096;
		powers[13] = 8192;
		powers[14] = 16384;
		powers[15] = 32768;
		powers[16] = 65536;
		powers[17] = 131072;
		powers[18] = 262144;
		powers[19] = 524288;
		powers[20] = 1048576;
		powers[21] = 2097152;
		powers[22] = 4194304;
		powers[23] = 8388608;
		powers[24] = 16777216;
		powers[25] = 33554432;
		powers[26] = 67108864;
		powers[27] = 134217728;
		powers[28] = 268435456;
		powers[29] = 536870912;
		powers[30] = 1073741824;
		powers[31] =-2147483648;
	}

	public int pow(int base, int exponent) {
		int result = 1;
		int i = 0;
		while (i < exponent) {
			result = result * base;
			i=i+1;
		}
		return result;
	}

	public int not_i(int a) {
		return -1 - a;
	}

	public int shr_i(int a, int b) {
		if (b > 31) {
			return 0;
		}
		if (a < 0) {
			a = a + powers[31];
			a = a / powers[b];
			a = a + powers[31 - b];
			return a;
		} else {
			return a / powers[b];
		}
	}

	public int shl_i(int a, int b) {
		if (b > 31) {
			return 0;
		}
		return a * powers[b];
	}

	public int and_i(int a, int b) {
		int c = 0;
		int x = 0;
		while (x <= 31) {
			c = c + c;
			if (a < 0) {
				if (b < 0) {
					c = c + 1;
				}
			}
			a = a + a;
			b = b + b;
			x=x+1;
		}
		return c;
	}

	public int or_i(int a, int b) {
		int c = 0;
		int x = 0;
		while (x <= 31) {
			c = c + c;
			if (a < 0) {
				c = c + 1;
			} else if (b < 0) {
				c = c + 1;
			}
			a = a + a;
			b = b + b;
			x=x+1;
		}
		return c;
	}

	public int xor_i(int a, int b) {
		int c = 0;
		int x = 0;
		while (x <= 31) {
			c = c + c;
			if (a < 0) {
				if (b >= 0) {
					c = c + 1;
				}
			} else if (b < 0) {
				c = c + 1;
			}
			a = a + a;
			b = b + b;
			x=x+1;
		}
		return c;
	}
}
