package boardgame;

import boardgame.console.ConsoleGame;
import javafx.application.Application;
import org.tinylog.Logger;

public class Main {

    private static final boolean PLAY_IN_CONSOLE = false;

    public static void main(String[] args) {
        if (PLAY_IN_CONSOLE) {
            Logger.info("A console game has been started");
            ConsoleGame.startConsoleGame();
        } else {
            Logger.info("A GUI game has been started");
            Application.launch(BoardGameApplication.class, args);
        }
    }
}
