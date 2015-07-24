package com.kendie.handlers;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.kendie.entities.Square;

public class Board {

	// WARNING: x is col, goes horizontal, y is row, goes vertical.
	// (0, 0) is the left bottom corner.
	private int col;
	private int row;
	private Square[][] squares;

	public enum Direction {
		UP, DOWN, RIGHT, LEFT
	};

	public Board() {
		col = 6; // default size
		row = 6;
		squares = new Square[col][row];
	}

	public Board(int c, int r) {
		col = c;
		row = r;
		squares = new Square[c][r];
	}

	public Board(Board other) {
		// TODO check size of boards if match
		col = other.col;
		row = other.row;
		squares = new Square[col][row];
		for (int i = 0; i < col; ++i) {
			for (int j = 0; j < row; ++j) {
				squares[i][j] = new Square(other.squares[i][j]);
			}
		}
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Square getSquare(int x, int y) {
		if (x < 0 || x > (col - 1) || y < 0 || y > (row - 1)) {
			return null;
		} else {
			return squares[x][y];
		}
	}

	public Square[][] getSquares() {
		return squares;
	}

	public void swap(int x1, int y1, int x2, int y2) {
		// if (x1 >= 0 && x1 < 8 &&
		// y1 >= 0 && y1 < 8 &&
		// x2 >= 0 && x2 < 8 &&
		// y2 >= 0 && y2 < 8) {

		Square temp = squares[x1][y1];
		squares[x1][y1] = squares[x2][y2];
		squares[x2][y2] = temp;
		// }
	}

	public void shift(int x1, int y1, Direction direction) {
		if (direction == Direction.RIGHT) {
			// shift X to right, keep Y
			Square temp = squares[getCol() - 1][y1];
			for (int i = getCol() - 1; i > 0 ; --i) {
				squares[i][y1] = squares[i - 1][y1];
			}
			squares[0][y1] = temp;
		}
		if (direction == Direction.LEFT) {
			// shift X to left, keep Y
			Square temp = squares[0][y1];
			for (int i = 0; i < getCol() - 1 ; ++i) {
				squares[i][y1] = squares[i + 1][y1];
			}
			squares[getCol() - 1][y1] = temp;
		}
		if (direction == Direction.UP) {
			// shift Y to up, keep X
			Square temp = squares[x1][getRow() - 1];
			for (int j = getRow() - 1; j > 0 ; --j) {
				squares[x1][j] = squares[x1][j - 1];
			}
			squares[x1][0] = temp;
		}
		if (direction == Direction.DOWN) {
			// shift Y to down, keep X
			Square temp = squares[x1][0];
			for (int j = 0; j < getRow() - 1 ; ++j) {
				squares[x1][j] = squares[x1][j + 1];
			}
			squares[x1][getRow() - 1] = temp;
		}
	}
	public void del(int x, int y) {
		// if (x >= 0 && x < 8 &&
		// y >= 0 && y < 8) {
		squares[x][y].setType(Square.Type.sqEmpty);
		// }
	}

	public void fillNew(int x, int y) {
		if (squares[x][y].equals(Square.Type.sqEmpty)) {
			squares[x][y].setType(Square.numToType(MathUtils.random(1, 5)));
		}
	}

	public void setEmpty(int x, int y) {
		squares[x][y].setType(Square.Type.sqEmpty);
	}
	public void generate() {
		boolean repeat = false;

		do {
			repeat = false;
			System.out.println("### Generating...");

			for (int i = 0; i < col; ++i) {
				for (int j = 0; j < row; ++j) {
					squares[i][j] = new Square(Square.numToType(MathUtils
							.random(1, 5))); // 6 types of square
					squares[i][j].mustFall = true;
					squares[i][j].origY = (int) MathUtils.random((row -1), (row * 2 - 1));
					squares[i][j].destY = j + squares[i][j].origY;

				}
			}

			if (!check().isEmpty()) {
				System.out.println("Generated board has matches, repeating...");
				repeat = true;
			}

			else if (solutions().isEmpty()) {
				System.out.println("Generated board doesn't have solutions, repeating...");
				repeat = true;
			}
		} while (repeat);

		System.out
				.println("The generated board has no matches but some possible solutions.");
	}

	public void gemsOutScreen() {
		for(int i = 0; i < col; ++i){
			for(int j = 0; j < row; ++j){
				squares[i][j].mustFall = true;
				squares[i][j].origY = j;
				squares[i][j].destY =  9 + MathUtils.random(1, (row - 1));
			}
		}
	}

	public void calcFallMovements() {
		for (int x = 0; x < col; ++x) {
			// From bottom to top
			for (int y = (row - 1); y >= 0; --y) {
				// origY stores the initial position in the fall
				squares[x][y].origY = y;

				// If square is empty, make all the squares above it fall
				if (squares[x][y].equals(Square.Type.sqEmpty)) {
					for (int k = y + 1; k < row; ++k) {
						squares[x][k].mustFall = true;
						squares[x][k].destY++;

						if (squares[x][k].destY > (row - 1)) {
							System.out.println("WARNING in calcFallMovements");
						}
					}
				}
			}
		}
	}

	public void applyFall() {
		for (int x = 0; x < col; ++x) {
			// From bottom to top in order not to overwrite squares
			for (int y = 0; y < row; ++y) {
				if (squares[x][y].mustFall == true
						&& !squares[x][y].equals(Square.Type.sqEmpty)) {
					int y0 = squares[x][y].destY;

					if (y - y0 < 0) {
						System.out.println("WARNING in applyFall()");
						System.out.println("y = " + y);
						System.out.println("y0 = " + y0);
					}

					squares[x][y - y0] = squares[x][y]; // logically swapped
					squares[x][y] = new Square(Square.Type.sqEmpty);
				}
			}
		}
	}

	public void fillSpaces() {
		for (int x = 0; x < col; ++x) {
			// Count how many jumps do we have to fall
			int jumps = 0;

			for (int y = 0; y < row; ++y) {
				if (!squares[x][y].equals(Square.Type.sqEmpty)) {
					break;
				}
				++jumps;
			}

			for (int y = 0; y < row; ++y) {
				if (squares[x][y].equals(Square.Type.sqEmpty)) {
					squares[x][y].setType(Square.numToType(MathUtils.random(1, 5)));
					squares[x][y].mustFall = true;
					squares[x][y].origY = y + jumps;
					squares[x][y].destY = jumps;
				}
			}
		}
	}

	public MultipleMatch check() {
		int k;

		MultipleMatch multiMatches = new MultipleMatch();

		for (int y = 0; y < row; ++y) {
			// First, we check each row (horizontal, col by col)
			for (int x = 0; x < (col - 2); ++x) { // match 3, so -2

				Match currentRow = new Match();
				currentRow.add(new Coord(x, y));

				for (k = x + 1; k < col; ++k) {
					if (squares[x][y].equals(squares[k][y])
							&& !squares[x][y].equals(Square.Type.sqEmpty)) {
						currentRow.add(new Coord(k, y));
					} else {
						break;
					}
				}

				if (currentRow.size() > 2) {
					multiMatches.add(currentRow);
				}

				x = k - 1; // TODO: check if should be x = k
			}
		}

		for (int x = 0; x < col; ++x) {
			for (int y = 0; y < (row - 2); ++y) {

				Match currentColumn = new Match();
				currentColumn.add(new Coord(x, y));

				for (k = y + 1; k < row; ++k) {
					if (squares[x][y].equals(squares[x][k])
							&& !squares[x][y].equals(Square.Type.sqEmpty)) {
						currentColumn.add(new Coord(x, k));
					} else {
						break;
					}
				}

				if (currentColumn.size() > 2) {
					multiMatches.add(currentColumn);
				}

				y = k - 1;
			}
		}

		return multiMatches;
	}

	public ArrayList<Coord> solutions() {
		ArrayList<Coord> results = new ArrayList<Coord>();

		if (!check().isEmpty()) {
			results.add(new Coord(-1, -1));
			return results;
		}

		/*
		 * Check all possible boards (49 * 4) + (32 * 2) although there are many
		 * duplicates
		 */
		Board temp = new Board(this);

		for (int x = 0; x < col; ++x) {
			for (int y = 0; y < row; ++y) {

				// Swap with the one above and check
				//temp.swap(x, y, x, y - 1);
				temp.shift(x, y, Direction.DOWN);
				if (!temp.check().isEmpty()) {
					results.add(new Coord(x, y));
				}
				//temp.swap(x, y, x, y - 1);
				temp.shift(x, y, Direction.UP);

				// Swap with the one below and check
				//temp.swap(x, y, x, y + 1);
				temp.shift(x, y, Direction.UP);
				if (!temp.check().isEmpty()) {
					results.add(new Coord(x, y));
				}
				//temp.swap(x, y, x, y + 1);
				temp.shift(x, y, Direction.DOWN);

				// Swap with the one on the left and check
				//temp.swap(x, y, x - 1, y);
				temp.shift(x, y, Direction.LEFT);
				if (!temp.check().isEmpty()) {
					results.add(new Coord(x, y));
				}
				//temp.swap(x, y, x - 1, y);
				temp.shift(x, y, Direction.RIGHT);

				// Swap with the one on the right and check
				//temp.swap(x, y, x + 1, y);
				temp.shift(x, y, Direction.RIGHT);
				if (!temp.check().isEmpty()) {
					results.add(new Coord(x, y));
				}
				//temp.swap(x, y, x + 1, y);
				temp.shift(x, y, Direction.LEFT);
			}
		}


		return results;
	}

	public void endAnimation() {
		for (int x = 0; x < col; ++x) {
			for (int y = 0; y < row; ++y) {
				squares[x][y].mustFall = false;
				squares[x][y].origY = y;
				squares[x][y].destY = 0;
			}
		}
	}

	public String toString() {
		String string = new String("");

		for (int i = 0; i < col; ++i) {
			for (int j = 0; j < row; ++j) {
				string += "(" + squares[i][j].origY + ", "
						+ squares[i][j].destY + ")  ";
			}

			string += "\n";
		}

		string += "\n";

		return string;
	}
}
