package boardgame.model;

import game.State;

/**
 * Represents a saved game state.
 * @param board the state of the board
 * @param nextPlayer the player who plays the next turn
 */
public record GameSave(Piece[][] board, State.Player nextPlayer) { }
