/**
 * Implements a computer connect four player using a heuristic minimax algorithm
 *
 * @author Jack Burns
 * @version 1.0
 * @since 2/2017
 */
 
class ComputerConnectFourPlayer implements ConnectFourPlayer {

	private static final int INFINITY = 1100000; //my infinity
	private static final int BOARD_OFFSET = 4; //for evaluating boards of all sizes
	private static final double NAN = Double.NaN;

	private byte side; // -1 or 1, depending on which side this is
	private int depth;
	private int[] moveList;

	/**
	 * Constructor for the computer player.
	 * @param depth the number of plies to look ahead
	 * @param side -1 or 1, depending on which player this is
	 */
	public ComputerConnectFourPlayer(int depth, byte side) {
		this.side = side;
		this.depth = depth;

		//holds the optimal move
		this.moveList = new int[1];
	}

	/**
	 * This computer is stupid. It always plays as far to the left as possible.
	 * @param rack the current rack
	 * @return the column to play
	 */
	public int getNextPlay(byte[][] rack) {

		//returns the move to make
		return minimax(rack, this.depth);
	}

	/**
	 * Minimax processes the recursion done by MINI and MAXI and gives a valid move
	 *
	 * @param rack (board)
	 * @return int depth (column to play)
	 */
	private int minimax(byte[][] rack, int depth) {

		//call MAXI
		int score = MAXI(rack, depth);

		if(score != NAN) {

			//return the side effect
			return this.moveList[0];
		}
		return -1;
	}


	/**
	 * MAX
	 *
	 * @param byte[][] rack (a board state)
	 * @param int absDepth (max depth of search)
	 * @param int currDepth (current depth in search)
	 * @return int max maximum value from evaluation of states
	 */
	private int MAXI(byte[][] rack, int absDepth) {

		//init
		int value = 0;
		int max = 0;
		int finalMove = -1000;
		byte currSide = this.side;

		//tests for terminal states and depth
		if(cutoff(rack) == true || absDepth == 0) {
			max = evaluate(rack);
		}

		else {
			max = -INFINITY;

			//expand all possible states
			for(int i = 0; i < rack[0].length; i++) {

				//check for playability
				if(isPlayable(rack, i)) {
					byte[][] node = sink(rack, i, currSide);
					value = MINI(node, absDepth-1);

					//if there is a new max, update that value and add that move as the last move
					if(value >= max) {
						max = value;
						finalMove = i;
						moveList[0] = finalMove;
					}
				}
			}
		}
		return max;
	}

	/**
	 * MIN
	 *
	 * @param byte[][] rack (a board state)
	 * @param int absDepth (max depth of search)
	 * @param int currDepth (current depth in search)
	 * @return int max maximum value from evaluation of states
	 */
	private int MINI(byte[][] rack, int absDepth) {

		//init
		int value = 0;
		int min = 0;
		int finalMove = -1000;
		byte currSide = (byte)(-this.side);

		//tests for terminal states and depth
		if(cutoff(rack) == true || absDepth == 0) {
			min = evaluate(rack);
		}

		else {

			min = INFINITY;

			//expand all possible states
			for(int i = 0; i < rack[0].length; i++) {

				//check for playability
				if(isPlayable(rack, i)) {

					byte[][] node = sink(rack, i, currSide);
					value = MAXI(node, absDepth-1);

					//if there is a new min, update that value and add that move as the last move
					if(value <= min) {
						min = value;
						finalMove = i;
						moveList[0] = finalMove;
					}
				}
			}
		}
		return min;
	}

	/**
	 * Returns if a column is playable or not
	 *
	 * @param int column
	 * @param byte[][] rack
	 * @return boolean playable (true or false depending on whether it is playable or not)
	 */
	private boolean isPlayable(byte[][] rack, int column) {
		return rack[0][column] == 0;
	}

	/**
	 * Applies modification to a board given an action
	 *
	 * @param byte[][] rack (board state)
	 * @param int column (action)
	 * @param byte side (side player is playing for)
	 * @return byte[][] that is the new state
	 */
	private byte[][] sink(byte[][] board, int column, byte currSide) {

		//make a copy of the rack as to not change the original
		byte[][] rack = copyRack(board);

		//if the column is playable
		if(isPlayable(rack, column) == true) {

			//for each row in the board
			for(int i = 0; i < rack.length; i++) {

				//if the row is not the last and there is a zero in the current row at that column
				if(i == rack.length - 1 && rack[i][column] == 0) {
					rack[i][column] = currSide;
					return rack;
				}

				//if there is a zero in the column and not second to last row
				else if(rack[i][column] == 0) {

					if(rack[i+1][column] == 0) {
						continue;
					}
					else {
						rack[i][column] = currSide;
						return rack;
					}
				}
			}
		}
		return rack;
	}

	// makes a copy of the rack, to prevent players from accessing it directly
	private static byte[][] copyRack(byte[][] rack) {

		byte[][] copy = new byte[rack.length][rack[0].length];

		for (int i=0; i<rack.length; i++) {
			System.arraycopy(rack[i], 0, copy[i], 0, rack[i].length);
		}

		return copy;
	}

	/**
	 * Decides is a node should be "cut off" or not
	 *
	 * @param byte[][] rack (the current state of the board)
	 * @param absDepth (the depth limit specified by user)
	 * @param int currDepth (the depth of the current board state)
	 * @return true if a node should be evaluated, false otherwise
	 */
	private boolean cutoff(byte[][] rack) {

		//a terminating state
		if(evaluate(rack) == INFINITY || evaluate(rack) == -INFINITY) {
			return true;
		}
		//is within the depth range and does not need to be cut off
		else {
			return false;
		}
	}

	/**
	 * Evaluation function for the computer player
	 *
	 * @param the current board
	 * @return int evaluation sum
	 */
	private int evaluate(byte[][] rack) {

		//sum and width/length of the board
		int sum = 0;
		int eval = 0;
		int width = rack[0].length;
		int length = rack.length;

		//for counting the number of tokens in a given span by each player
		int currentPCount = 0;
		int altPCount = 0;
		byte altSide = (byte)(-side);
		byte side = this.side;

		//iterate through the board to get each possible span of 4 contiguous slots
		for(int i = length - 1; i >= 0; i--) {
			for(int j = 0; j < width; j++) {

				//work the horizontal, iterating across a row
				if(j <= width - BOARD_OFFSET) {

					for(int k = j; k <= j + 3; k++) {
						if(rack[i][k] == side) {
							currentPCount++;
						}
						else if(rack[i][k] == altSide) {
							altPCount++;
						}
					}

					//add evaluation to sum and then reset counters
					eval = evalHelper(currentPCount, altPCount);

					if(eval == INFINITY || eval == -INFINITY) {
						return eval;
					}

					sum += eval;
					currentPCount = 0;
					altPCount = 0;
				}

				//work the vertical, iterating up through each column
				if(i >= 3) {

					for(int k = i; k >= i - 3; k--) {

						if(rack[k][j] == side) {
							currentPCount++;
						}
						else if(rack[k][j] == altSide) {
							altPCount++;
						}
					}

					//add evaluation to sum and then reset counters
					eval = evalHelper(currentPCount, altPCount);

					if(eval == INFINITY || eval == -INFINITY) {
						return eval;
					}

					sum += eval;
					currentPCount = 0;
					altPCount = 0;
				}

				//work the diagonal positive slope
				if((i >= 3) && (j <= width - BOARD_OFFSET)) {

					int h = j;
					for(int k = i; k >= i - 3; k--) {

							if(rack[k][h] == side) {
								currentPCount++;
							}
							else if(rack[k][h] == altSide) {
								altPCount++;
							}

							h++;
					}

					//add evaluation to sum and then reset counters
					eval = evalHelper(currentPCount, altPCount);

					if(eval == INFINITY || eval == -INFINITY) {
						return eval;
					}

					sum += eval;
					currentPCount = 0;
					altPCount = 0;
				}

				//work the diagonal negative slope
				if((i >= 3) && (j >= 3)) {

					int h = j;
					for(int k = i; k >= i - 3; k--) {

							if(rack[k][h] == side) {
								currentPCount++;
							}
							else if(rack[k][h] == altSide) {
								altPCount++;
							}

							h--;
					}

					//add evaluation to sum and then reset counters
					eval = evalHelper(currentPCount, altPCount);

					if(eval == INFINITY || eval == -INFINITY) {
						return eval;
					}

					sum += eval;
					currentPCount = 0;
					altPCount = 0;
				}
			}
		}
		return sum;
	}

	/**
	 * Evaluation helper to decide what to add to sum
	 *
	 * @param int currentPCount (tokens of the current player in a span of 4 spots)
	 * @param int altPCount (tokens of the alt player in a span of 4 spots)
	 * @return score (the evaluation of those four spots)
	 */
	private int evalHelper(int currentPCount, int altPCount) {

		//if there are 4
		if(currentPCount == 4) {
			return INFINITY;
		}

		//if there are 3
		else if(currentPCount == 3 && altPCount == 0) {
			return 100;
		}

		//2
		else if(currentPCount == 2 && altPCount == 0) {
			return 10;
		}

		//1
		else if(currentPCount == 1 && altPCount == 0) {
			return 1;
		}

		else if(altPCount == 4) {
			return -INFINITY;
		}

		else if(altPCount == 3 && currentPCount == 0) {
			return -100;
		}

		else if(altPCount == 2 && currentPCount == 0) {
			return -10;
		}

		else if(altPCount == 1 && currentPCount == 0) {
			return -1;
		}

		else {
			return 0;
		}
	}
}
