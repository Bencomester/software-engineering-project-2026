package boardgame;

import boardgame.console.ConsoleGame;
import javafx.application.Application;

public class Main {

    private static final boolean PLAY_IN_CONSOLE = false;

    public static void main(String[] args) {
        if (PLAY_IN_CONSOLE) {
            ConsoleGame.startConsoleGame();
        } else {
            Application.launch(BoardGameApplication.class, args);
        }
    }
}
