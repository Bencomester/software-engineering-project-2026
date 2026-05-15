package boardgame;

import boardgame.console.ConsoleGame;

public class Main {

    private static final boolean PLAY_IN_CONSOLE = true;

    public static void main(String[] args) {
        if (PLAY_IN_CONSOLE) {
            ConsoleGame.startConsoleGame();
        } else {
            throw new RuntimeException("Currently only available in the console!");
        }
    }
}
