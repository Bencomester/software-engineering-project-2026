package boardgame;

import boardgame.console.ConsoleGame;
import boardgame.ui.BoardGameApplication;
import javafx.application.Application;
import org.tinylog.Logger;

public final class Main {

    /**
     * Constant value for changing between console and GUI.
     */
    private static final boolean PLAY_IN_CONSOLE = false;

    private Main() { }

    /**
     * Main method of the project.
     * @param args run parameters
     */
    public static void main(final String[] args) {
        if (PLAY_IN_CONSOLE) {
            Logger.info("A console game has been started");
            ConsoleGame.startConsoleGame();
        } else {
            Logger.info("A GUI game has been started");
            Application.launch(BoardGameApplication.class, args);
        }
    }
}
