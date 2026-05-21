package boardgame.model;

/**
 * Represents the result of a finished game.
 * @param player1 the name of the first player
 * @param player2 the name of the second player
 * @param winner the name of the winning player
 * @param date the time and date when the match ended
 */
public record GameResult(
        String player1,
        String player2,
        String winner,
        String date) { }
