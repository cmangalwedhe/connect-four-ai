import java.util.List;
import java.util.ArrayList;

public class GraderRunner {
    // play the game many times and get win/lose/tie/invalid statistics from them
    public static final int NUM_GAMES = 1000;

    // Declaring ANSI_RESET so that we can reset the color
    public static final String ANSI_RESET = "\u001B[0m";
    // Declaring the background color
    public static final String ANSI_RED_BACKGROUND
            = "\u001B[41m";

    public static void main(String[] args) {
        List<GamesStatistics> yellowStatistics = playMyAgentAsYellow();
        List<GamesStatistics> redStatistics = playMyAgentAsRed();

        // calculate grade - out of 104% plus bonus vs. Hulett/Minimax
        double grade = 0;
        double[] grades = {.1, .1, .1, .1, .12, .06, .06};
        int index = 0;
        boolean flagInvalidMove = false;


        // Once all games have been played, print out the statistics
        System.out.println("\033[33mGame Statistics for Yellow MyAgent:\033[0m");
        for (GamesStatistics gameStats : yellowStatistics) {
            gameStats.printStatistics();
            if(gameStats.numInvalidMoves > 0)flagInvalidMove = true;
            grade += gameStats.percentTotal(gameStats.numYellowWins) * grades[index++];
        }

        System.out.println();

        index = 0;
        System.out.println("\033[31mGame Statistics for Red MyAgent:\033[0m");
        for (GamesStatistics gameStats : redStatistics) {
            gameStats.printStatistics();
            if(gameStats.numInvalidMoves > 0)flagInvalidMove = true;
            grade += gameStats.percentTotal(gameStats.numRedWins) * grades[index++];
        }

        System.out.println("----------------");
        grade -= flagInvalidMove? 10: 0;
        System.out.printf(ANSI_RED_BACKGROUND + "Project Grade: %.2f" + ANSI_RESET, grade);


        System.out.println();
        System.exit(0); // Stop the game; this closes any Java swing hullabaloo.
    }

    /**
     * Plays
     *
     * @return
     */
    public static List<GamesStatistics> playMyAgentAsYellow() {
        Connect4Game game = new Connect4Game(7, 6); // create the game; these sizes can be altered for larger or smaller games
        Agent myAgent = new MyAgent(game, false); // create the yellow player, any subclass of Agent

        // the agents who are going to play against MyAgent
        List<Agent> redAgents = new ArrayList<Agent>();
        redAgents.add(new RandomAgent(game, true));
        redAgents.add(new BeginnerAgent(game, true));
        redAgents.add(new IntermediateAgent(game, true));
        redAgents.add(new AdvancedAgent(game, true));
        redAgents.add(new BrilliantAgent(game, true));
        // redAgents.add(new HulettAgent(game, true));


        // Play all games
        List<GamesStatistics> games = new ArrayList<GamesStatistics>();
        for (Agent agent : redAgents) {
            games.add(playGame(NUM_GAMES, game, agent, myAgent));
        }

        redAgents.add(new MyAgent(game, true));
        games.add(playGame(10, game, redAgents.get(redAgents.size() - 1), myAgent));

        return games;
    }

    public static List<GamesStatistics> playMyAgentAsRed() {
        Connect4Game game = new Connect4Game(7, 6); // create the game; these sizes can be altered for larger or smaller games
        Agent myAgent = new MyAgent(game, true); // create the yellow player, any subclass of Agent


        // the agents who are going to play against MyAgent
        List<Agent> redAgents = new ArrayList<Agent>();
        redAgents.add(new RandomAgent(game, false));
        redAgents.add(new BeginnerAgent(game, false));
        redAgents.add(new IntermediateAgent(game, false));
        redAgents.add(new AdvancedAgent(game, false));
        redAgents.add(new BrilliantAgent(game, false));
        // redAgents.add(new HulettAgent(game, false));


        // Play all games
        List<GamesStatistics> games = new ArrayList<GamesStatistics>();
        for (Agent agent : redAgents) {
            games.add(playGame(NUM_GAMES, game, myAgent, agent));
        }
        redAgents.add(new MyAgent(game, false));
        games.add(playGame(10, game, myAgent, redAgents.get(redAgents.size() - 1)));
        return games;
    }

    /**
     * Plays the games NUM_GAMES times and tracks the game results (wins/ties)
     *
     * @param NUM_GAMES    the number of games to play
     * @param game         the connect 4 game to play with
     * @param redPlayer    the red player of the game.
     * @param yellowPlayer the yellow player of the game.
     * @return the GamesStatistics from playing these games
     */
    public static GamesStatistics playGame(final int NUM_GAMES, Connect4Game game, Agent redPlayer, Agent yellowPlayer) {
        GamesStatistics gameStatistics = new GamesStatistics(redPlayer, yellowPlayer);
        gameStatistics.numGames = NUM_GAMES;

        Connect4Frame gameFrame = new Connect4Frame(game, redPlayer, yellowPlayer); // create the game window

        // play the game
        for (int i = 0; i < NUM_GAMES; i++) {
            gameFrame.newGame();
            gameFrame.playToEnd();

            if (game.gameWon() == 'R') {
                gameStatistics.numRedWins++;
            } else if (game.gameWon() == 'Y') {
                gameStatistics.numYellowWins++;
            } else if (game.boardFull()) {
                gameStatistics.numTies++;
            }
        }
        gameFrame.dispose();
        return gameStatistics;
    }
}
