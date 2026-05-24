package boardgame.model;

import game.State;

/**
 * Represents a saved game state.
 * @param board the state of the board
 * @param nextPlayer the player who plays the next turn
 * @param player1name the name of the first player
 * @param player2name the name of the second player
 */
public record GameSave(
        Piece[][] board,
        State.Player nextPlayer,
        String player1name,
        String player2name) { }
