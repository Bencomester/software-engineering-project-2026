package boardgame.console;

import boardgame.model.BoardGameModel;
import common.util.board.Position;
import game.console.Game;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Makes possible to play the board game from the console.
 */
public final class ConsoleGame {

    /**
     * Replaces default constructor, which is desirable for a utility class.
     */
    private ConsoleGame() { }

    /**
     * Starts the board game played in the console.
     */
    public static void startConsoleGame() {
        BoardGameModel model = new BoardGameModel();
        Game<Position, BoardGameModel> game =
                new Game<>(model, ConsoleGame::parseMoveFromString);
        game.start();
    }

    /**
     * Converts string inputs to {@link Position}.
     * The string should be in the following format: {@code n m},
     * where n and m are integers.
     * @param moveString input string to convert
     * @return a position matching the input string
     * @throws IllegalArgumentException if the string doesn't match
     * the required format
     */
    private static Position parseMoveFromString(final String moveString) {
        Scanner sc = new Scanner(moveString);
        try {
            int x = sc.nextInt();
            int y = sc.nextInt();
            return new Position(x, y);
        }  catch (InputMismatchException e) {
            throw new IllegalArgumentException();
        }
    }

}
