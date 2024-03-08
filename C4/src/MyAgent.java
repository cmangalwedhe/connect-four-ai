import java.util.ArrayList;
import java.util.Random;

/**
 * @author Chinmay Mangalwedhe and Rohan Sheshadri
 * @version v4.1
 *
 * The following project was to create an Agent to play the popular game Connect Four. As a challenge, we took it upon
 * ourselves to implement the Minimax Decision Tree alongside Alpha-Beta Pruning to determine the most optimal move for
 * our agent to take.
 */

public class MyAgent extends Agent {
    private int lastColumnVisited;
    private final int SIMULATION_DEPTH = 8; // tells the tree how many moves ahead to check
    Random r;
    private static final int[] COLUMN_PRIORITY = {3, 2, 4, 1, 5, 0, 6}; // check columns in this priority

    /**
     * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
     *
     * @param game   The game the agent will be playing.
     * @param iAmRed True if the agent is Red, False if the agent is Yellow.
     */
    public MyAgent(Connect4Game game, boolean iAmRed) {
        super(game, iAmRed);
        r = new Random();
    }

    /**
     * The move method is run every time it is this agent's turn in the game. You may assume that
     * when move() is called, the game has at least one open slot for a token, and the game has not
     * already been won.
     * <p>
     * By the end of the move method, the agent should have placed one token into the game at some
     * point.
     * <p>
     * After the move() method is called, the game engine will check to make sure the move was
     * valid. A move might be invalid if: - No token was place into the game. - More than one token
     * was placed into the game. - A previous token was removed from the game. - The color of a
     * previous token was changed. - There are empty spaces below where the token was placed.
     * <p>
     * If an invalid move is made, the game engine will announce it and the game will be ended.
     */
    public void move() {
        Connect4Game simulationGame = new Connect4Game(myGame);
        int[] res = minimax(simulationGame, SIMULATION_DEPTH, Integer.MAX_VALUE, Integer.MIN_VALUE, true);
        moveOnColumn(res[0]);
    }

    /**
     * The following method is minimax, it tries multiple combinations with a weighted heuristic to find the
     * most optimal move for the player. The code is based off "maximizing" an arbitrary score for our agent and
     * "minimizing" the score for the opponent. The scores are determined based off a number of patterns found in
     * winning moves all throughout Connect Four (diagonals, four in a row, four in a column, etc).
     * @param simulationGame a copy of the current game board to prevent cross-over from the simulation to the real game
     * @param depth tells the method how many moves further to look at
     * @param beta the "beta" value used for pruning the tree
     * @param alpha the "alpha" value used for pruning the tree
     * @param isMaximizingPlayer tells whether the current player should be maximized or minimized (opponent or player)
     * @return an array of two integers: one of the column with the optimal move and the score given for said move
     */
    private int[] minimax(Connect4Game simulationGame, int depth, int beta, int alpha, boolean isMaximizingPlayer) {
        // base case - stops when a leaf is met, when the board is full, or someone has won
        if (depth <= 0 || simulationGame.boardFull() || simulationGame.gameWon() != 'N') {
            return new int[]{lastColumnVisited, evaluateBoard(simulationGame, depth)};
        }

        // maximizes the player
        int optimalColumn = Integer.MIN_VALUE;
        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (int col : COLUMN_PRIORITY) {
                if (simulationGame.getColumn(col).getIsFull()) continue;

                // places a token at a spot and then calculates the score determined by the heuristic
                lastColumnVisited = col;
                int tempInsertRow = getLowestEmptyIndex(simulationGame.getColumn(col));
                placeTheoreticalToken(simulationGame, col, tempInsertRow, iAmRed);
                int evaluation = minimax(simulationGame, depth - 1, beta, alpha, false)[1];
                simulationGame.getColumn(col).getSlot(tempInsertRow).clear();

                // if greater than current evaluation, then the optimal move would be in the new column
                alpha = Math.max(evaluation, alpha);
                if (evaluation > maxEval) {
                    maxEval = evaluation;
                    optimalColumn = col;
                }

                // alpha-beta pruning: this will occur when it is found it is not necessary to search the other
                // children in this tree as there is a better option no matter what the children are
                if (beta <= alpha) break;
            }

            // returns the ultimate optimal move with column (needed for placement) and score for recursion
            return new int[]{optimalColumn, maxEval};
        } else {
            // this minimizes the opponent's move and chance to win
            int minEval = Integer.MAX_VALUE;

            for (int col : COLUMN_PRIORITY) {
                if (simulationGame.getColumn(col).getIsFull()) continue;

                lastColumnVisited = col;
                int tempInsertRow = getLowestEmptyIndex(simulationGame.getColumn(col));
                placeTheoreticalToken(simulationGame, col, tempInsertRow, !iAmRed);
                int eval = minimax(simulationGame, depth - 1, beta, alpha, true)[1];
                simulationGame.getColumn(col).getSlot(tempInsertRow).clear();
                beta = Math.min(eval, beta);

                if (eval < minEval) {
                    optimalColumn = col;
                    minEval = eval;
                }

                if (beta <= alpha) break;
            }

            return new int[]{optimalColumn, minEval};
        }
    }

    /**
     * For the simulation, multiple tokens must be "dropped" on the board for testing purposes. As a result, this method
     * drops a token for testing purposes and is later analyzed by the heuristic for a score to come up
     * @param simulationGame a copy of the board to prevent crossover with the real game
     * @param col the column to "drop" the token onto the board
     * @param row the row to "drop" the token onto the board
     * @param red is the color red? if so, make token red, otherwise yellow.
     */
    private void placeTheoreticalToken(Connect4Game simulationGame, int col, int row, boolean red) {
        if (red)
            simulationGame.getColumn(col).getSlot(row).addRed();
        else
            simulationGame.getColumn(col).getSlot(row).addYellow();
    }

    /**
     * Evaluates the board and comes up with the score. The method has it weighted where some positions are more
     * important than others. For example, for three of the same color tokens in a row is worth 1000 "points" while two in
     * the same row would be worth 500 "points." This is to ensure that moves that can the user to win will be favored
     * than a move that is not there yet.
     *
     * @param simulationGame a copy of the board to prevent crossover with the real game
     * @param depth the depth from the minimax method - how many moves further to check
     * @return the "score" given by the heuristic for minimax to interpret
     */
    private int evaluateBoard(Connect4Game simulationGame, int depth) {
        int score = 0;
        char[][] board = simulationGame.getBoardMatrix();

        if (simulationGame.gameWon() == (iAmRed ? 'R' : 'Y')) {
            return 100000 + depth;
        }

        if (simulationGame.gameWon() == (!iAmRed ? 'Y' : 'R')) {
            return -100000 - depth;
        }

        // checks vertically to see if an open slot may exist
        ArrayList<Integer> emptyColumns = getEmptyColumns(simulationGame);

        for (int emptyColumn : emptyColumns) {
            int lowestEmptyRow = getLowestEmptyIndex(simulationGame.getColumn(emptyColumn));
            if (lowestEmptyRow >= simulationGame.getRowCount() - 3) continue;

            score += findSubsetScore(new char[]{board[lowestEmptyRow][emptyColumn],
                    board[lowestEmptyRow + 1][emptyColumn], board[lowestEmptyRow + 2][emptyColumn],
                    board[lowestEmptyRow + 3][emptyColumn]});
        }

        // checks horizontally to see if an open slot may exist
        for (int i = simulationGame.getRowCount() - 1; i >= 0; i--) {
            for (int j = 0; j < simulationGame.getColumnCount() - 3; j++) {
                char[] row = board[i];
                score += (int) (findSubsetScore(new char[]{row[j], row[j + 1], row[j + 2], row[j + 3]}) * 1.75);
            }
        }

        // following two loops check diagonals (forwards and backwards)
        for (int i = 1; i < simulationGame.getColumnCount(); i++) {
            for (int j = simulationGame.getRowCount() - i; j >= 2; j--) {
                score += findSubsetScore(new char[]{board[j][i], board[j - 1][i + 1], board[j - 2][i + 2]});
            }

            for (int j = 0; j < simulationGame.getRowCount() - i - 1; j++) {
                score += findSubsetScore(new char[]{board[j][i], board[j+1][i+1], board[j+2][i+2]});
            }
        }

        for (int i = 2; i < simulationGame.getRowCount(); i++) {
            for (int j = 0; j < i - 1; j++) {
                score += findSubsetScore(new char[]{board[i][j], board[i - 1][j + 1], board[i - 2][j + 2]});
            }

            for (int j = simulationGame.getRowCount() - 1; j >= 2 + simulationGame.getRowCount() - i; j--) {
                score += findSubsetScore(new char[]{board[j][i], board[j - 1][i - 1], board[j - 2][i - 2]});
            }
        }

        return score;
    }

    /**
     * Returns how many empty columns there are on the simulation board
     * @param simulationGame a copy of the board to prevent crossover with the real game
     * @return a list of indices pertaining to columns that are not full
     */
    private ArrayList<Integer> getEmptyColumns(Connect4Game simulationGame) {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < simulationGame.getColumnCount(); i++) {
            if (!simulationGame.getColumn(i).getIsFull())
                list.add(i);
        }

        return list;
    }

    /**
     * Finds a "score" for a subset of the board. This is helpful as a method can be passed in rather than repeatedly
     * checking the score for different patterns.
     * @param subset a subset of the board.
     * @return a score pertaining to the subset.
     */
    private int findSubsetScore(char[] subset) {
        int blankSpots = blankSpotsCount(subset);

        if (blankSpots == 4) return 0;

        int selfSpots = selfSpotsCount(subset);
        int enemySpots = enemySpotsCount(subset);

        if (selfSpots == 3 && blankSpots == 1) return 1000;
        if (enemySpots == 3 && blankSpots == 1) return -1000;

        if (selfSpots == 2 && enemySpots == 0) return 500;
        if (enemySpots == 2 && selfSpots == 0) return -500;

        // if no concerning pattern is found, it's neutral
        return 0;
    }

    /**
     * Counts the amount of blank spots in a certain subset
     * @param ary a subset of the board in a character array
     * @return the amount of blank spots (denoted by 'b') in ary
     */
    private int blankSpotsCount(char[] ary) {
        int cnt = 0;

        for (int i = 0; i < ary.length; i++) {
            if (ary[i] == 'B') {
                cnt++;
            }
        }

        return cnt;
    }

    /**
     * Counts the amount of self (player) spots in a certain subset
     * @param ary a subset of the board
     * @return the amount of blank spots (denoted by the color) in ary
     */
    private int selfSpotsCount(char[] ary) {
        int cnt = 0;

        for (int i = 0; i < ary.length; i++) {
            if (ary[i] == (iAmRed ? 'R' : 'Y')) {
                cnt++;
            }
        }

        return cnt;
    }

    /**
     * Counts the amount of enemy (opponent) spots in a certain subset
     * @param ary a subset of the board
     * @return the amount of blank spots (denoted by the color) in ary
     */
    private int enemySpotsCount(char[] ary) {
        int cnt = 0;
        for (int i = 0; i < ary.length; i++) {
            if (ary[i] == (iAmRed ? 'Y' : 'R')) {
                cnt++;
            }
        }

        return cnt;
    }

    /**
     * Drops a token into a particular column so that it will fall to the bottom of the column. If
     * the column is already full, nothing will change.
     *
     * @param columnNumber The column into which to drop the token.
     */
    public void moveOnColumn(int columnNumber) {
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
        // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1) // if the column is not full
        {
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index
            if (iAmRed) // If the current agent is the Red player...
            {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            } else // If the current agent is the Yellow player (not the Red player)...
            {
                lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
            }
        }
    }

    /**
     * Returns the index of the top empty slot in a particular column.
     *
     * @param column The column to check.
     * @return the index of the top empty slot in a particular column; -1 if the column is already
     * full.
     */
    public int getLowestEmptyIndex(Connect4Column column) {
        int lowestEmptySlot = -1;
        for (int i = 0; i < column.getRowCount(); i++) {
            if (!column.getSlot(i).getIsFilled()) {
                lowestEmptySlot = i;
            }
        }
        return lowestEmptySlot;
    }

    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName() {
        return "ChinmayAgent";
    }
}