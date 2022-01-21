class Main {
	public static void main(String[] args) {
		GameOfLife gof = new GameOfLife();
		gof.init(10, 10);

		gof.set(4, 4, true);
		gof.set(4, 5, true);
		gof.set(4, 6, true);
		gof.set(5, 4, true);
		gof.set(5, 5, true);
		gof.set(5, 6, true);
		gof.set(6, 4, true);
		gof.set(6, 5, true);
		gof.set(6, 6, true);

		gof.printBoard();

		int i = 0;
		while (i < 10) {
			gof.update();
			gof.printBoard();
			i = i + 1;
		}
	}
}

class GameOfLife {
	public boolean[][] currentTiles;
	public boolean[][] backupTiles;
	public int width;
	public int height;

	public void init(int width, int height) {
		this.width = width;
		this.height = height;

		currentTiles = new boolean[width][];
		backupTiles = new boolean[width][];

		int i = 0;
		while (i < width) {
			currentTiles[i] = new boolean[height];
			backupTiles[i] = new boolean[height];
			i = i + 1;
		}
	}

	public boolean get(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return false;
		}
		return this.currentTiles[x][y];
	}

	public void set(int x, int y, boolean b) {
		this.currentTiles[x][y] = b;
	}

	public void setNew(int x, int y, boolean b) {
		this.backupTiles[x][y] = b;
	}

	public int countNeighbors(int x, int y) {
		int count = 0;

		int dy = -1;
		while (dy <= 1) {

			int dx = -1;
			while (dx <= 1) {

				if ((dy != 0 || dx != 0) && get(x + dx, y + dy))
					count = count + 1;

				dx = dx + 1;
			}

			dy = dy + 1;
		}

		return count;
	}

	public void update() {
		int y = 0;
		while (y < height) {
			int x = 0;
			while (x < width) {

				int neighbors = countNeighbors(x, y);

				if (get(x, y)) {
					if (neighbors < 2) {
						setNew(x, y, false);
					} else if (neighbors <= 3) {
						setNew(x, y, true);
					} else {
						setNew(x, y, false);
					}
				} else {
					setNew(x, y, neighbors == 3);
				}

				x = x + 1;
			}
			y = y + 1;
		}

		boolean[][] tmp = currentTiles;
		currentTiles = backupTiles;
		backupTiles = tmp;
	}

	public void printBoard() {
		int y = height - 1;
		
		while (y >= 0) {
			int x = 0;

			while (x < width) {

				if (get(x, y)) {
					System.out.write(35);
				} else {
					System.out.write(46);
				}

				x = x + 1;
			}

			System.out.write(10);

			y = y - 1;
		}

		System.out.flush();
	}
}
