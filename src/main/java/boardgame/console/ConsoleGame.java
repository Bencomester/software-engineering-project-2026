package boardgame.console;

import boardgame.model.BoardGameModel;
import common.util.board.Position;
import game.console.Game;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleGame {

    public static void startConsoleGame() {
        BoardGameModel model = new BoardGameModel();
        Game<Position, BoardGameModel> game = new Game<>(model, ConsoleGame::parseMoveFromString);
        game.start();
    }

    public static Position parseMoveFromString(String moveString) {
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
