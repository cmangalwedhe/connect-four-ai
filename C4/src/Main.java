
/**
 * The main driver of the program. This file will create the game, create the two agents,
 * and create the window for the game. After that, Connect4Frame runs everything.
 */

public class Main {
    public static void main(String[] args) {
        Connect4Game game = new Connect4Game(7, 6); // create the game; these sizes can be altered for larger or smaller games
        Agent redPlayer = new MyAgent(game, true); // create the red player, any subclass of Agent
        Agent yellowPlayer = new BrilliantAgent(game, false); // create the yellow player, any subclass of Agent

        Connect4Frame mainframe = new Connect4Frame(game, redPlayer, yellowPlayer); // create the game window

        // uncomment
        // test your agent 1000 times
        /*int me = 0, them = 0, draw = 0;
        int times = 1000;
        for (int i = 0; i < times; i++) {
            mainframe.newGameButtonPressed();
            mainframe.playToEndButtonPressed();
            if (game.gameWon() == 'R') {
                me++;
            } else if (game.gameWon() == 'Y') {
                them++;
            } else {
                draw++;
            }
        }

         */

        System.out.println("Wins: " + me + "\nLosses: " + them + "\nDraws: " + draw
                + "\nOutcome: " + me / (double) 1000 * 100 + "%");
    }
}
