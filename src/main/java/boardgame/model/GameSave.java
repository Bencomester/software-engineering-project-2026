package boardgame.model;

import game.State;

public record GameSave(Piece[][] board, State.Player nextPlayer) {}
