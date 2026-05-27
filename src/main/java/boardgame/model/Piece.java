package boardgame.model;

/**
 * Represents the different piece types possible on the board.
 */
public enum Piece {

    /**
     * Represents an empty space, without a piece.
     */
    NONE,

    /**
     * Represents a red piece.
     */
    RED,

    /**
     * Represents a yellow piece.
     */
    YELLOW,

    /**
     * Represents a green piece.
     */
    GREEN;

    /**
     * Returns a piece with the next color in order.
     * @return the piece with the next color
     * @throws IllegalStateException if called on {@link #GREEN} piece
     */
    public Piece nextPiece() {
        return switch (this) {
            case NONE -> RED;
            case RED -> YELLOW;
            case YELLOW -> GREEN;
            case GREEN -> throw new IllegalStateException();
        };
    }
}
