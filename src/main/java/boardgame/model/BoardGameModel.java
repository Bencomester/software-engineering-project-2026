package boardgame.model;

import common.util.board.Position;
import game.State;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.tinylog.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation and logic model of the board game.
 * The model handles different states, moving logics and win conditions.
 * The class implements the {@link State} interface
 * taken from homework-project-utils-2026.
 * @author Bencomester
 */
public class BoardGameModel implements State<Position, BoardGameModel> {

    /**
     * The size of the game board,
     * which will be {@code BOARD_SIZE} × {@code BOARD_SIZE}.
     */
    private static final int BOARD_SIZE = 3;

    /**
     * The board as an array of arrays,
     * where each {@link Piece} is wrapped in a read-only wrapper.
     */
    private ReadOnlyObjectWrapper<Piece>[][] gameBoard;

    /**
     * The {@link Player} which makes the next move
     * wrapped in a read-only wrapper.
     */
    private ReadOnlyObjectWrapper<Player> nextPlayer;


    /**
     * Creates a square game board of size {@link #BOARD_SIZE}
     * and sets starting player.
     */
    @SuppressWarnings("unchecked")
    public BoardGameModel() {
        nextPlayer = new ReadOnlyObjectWrapper<>(Player.PLAYER_1);
        gameBoard = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j] = new ReadOnlyObjectWrapper<>(Piece.NONE);
            }
        }
        Logger.info("Created a new BoardGameModel");
    }


    /**
     * Returns the next {@link Player} wrapped in a read-only property.
     * @return read-only property containing the next player
     */
    public ReadOnlyObjectProperty<Player> getNextPlayerProperty() {
        return nextPlayer.getReadOnlyProperty();
    }

    /**
     * Returns the {@link Player} who has the next turn.
     * @return the Player who has the next turn
     */
    @Override
    public Player getNextPlayer() {
        return nextPlayer.get();
    }

    /**
     * Returns the {@link Piece} property at the specified square.
     *
     * @param row The index of the row specified
     * @param col The index of the column specified
     * @return ReadOnlyProperty of a {@link Piece}
     */
    public ReadOnlyObjectProperty<Piece> getPieceProperty(final int row,
                                                          final int col) {
        if (isOnTheBoard(new Position(row, col))) {
            return gameBoard[row][col].getReadOnlyProperty();
        }
        return new ReadOnlyObjectWrapper<>(Piece.NONE);
    }

    /**
     * Returns the {@link Piece} at the specified square.
     * @param row The index of the row specified
     * @param col The index of the column specified
     * @return the {@link Piece} at the specified square
     */
    public Piece getPiece(final int row, final int col) {
        return gameBoard[row][col].get();
    }

    /**
     * Resets the board game to the starting position.
     */
    public void resetGameBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j].set(Piece.NONE);
            }
        }

        nextPlayer.set(Player.PLAYER_2);
        nextPlayer.set(Player.PLAYER_1);
        Logger.info("Game Board reset");
    }

    /**
     * Checks if the game is over.
     * The game is over when there is three of the same color
     * in either row column or diagonal
     * @return {@code true} if the game has ended,
     * {@code false} if it's still ongoing
     */
    @Override
    public boolean isGameOver() {
        return checkRows()
                || checkColumns()
                || checkDiagonalFromTopLeft()
                || checkDiagonalsFromBottomLeft();
    }

    /**
     * Check if there is a row with the same three colors.
     * @return {@code true} if a colored row is found
     */
    @SuppressWarnings("DuplicatedCode")
    private boolean checkRows() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean areRowPiecesSame = true;
            Piece piece = gameBoard[i][0].get();
            if (piece == Piece.NONE) {
                continue;
            }

            for (int j = 1; j < BOARD_SIZE; j++) {
                if (piece != gameBoard[i][j].get()) {
                    areRowPiecesSame = false;
                    break;
                }
            }

            if (areRowPiecesSame) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if there is a column with the same three colors.
     * @return {@code true} if a colored column is found
     */
    @SuppressWarnings("DuplicatedCode")
    private boolean checkColumns() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean areRowPiecesSame = true;
            Piece piece = gameBoard[0][i].get();
            if (piece == Piece.NONE) {
                continue;
            }

            for (int j = 1; j < BOARD_SIZE; j++) {
                if (piece != gameBoard[j][i].get()) {
                    areRowPiecesSame = false;
                    break;
                }
            }

            if (areRowPiecesSame) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the primary diagonal has the same three colors.
     * @return {@code true} if the diagonal has the same colors
     */
    private boolean checkDiagonalFromTopLeft() {
        Piece piece = gameBoard[0][0].get();
        if (piece == Piece.NONE) {
            return false;
        }

        for (int i = 1; i < BOARD_SIZE; i++) {
            if (piece != gameBoard[i][i].get()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the secondary diagonal has the same three colors.
     * @return {@code true} if the diagonal has the same colors
     */
    private boolean checkDiagonalsFromBottomLeft() {
        Piece piece = gameBoard[BOARD_SIZE - 1][0].get();
        if (piece == Piece.NONE) {
            return false;
        }

        for (int i = 1; i < BOARD_SIZE; i++) {
            if (piece != gameBoard[BOARD_SIZE - i - 1][i].get()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the {@link Status} of the current board game.
     * @return {@link Status#IN_PROGRESS} if the game is still in progress,
     * {@link Status#PLAYER_1_WINS} if Player1 has won,
     * and {@link Status#PLAYER_2_WINS} if Player2 has won
     */
    @Override
    public Status getStatus() {
        if (isGameOver()) {
            return switch (nextPlayer.get()) {
                case PLAYER_1 -> Status.PLAYER_2_WINS;
                case PLAYER_2 -> Status.PLAYER_1_WINS;
            };
        }

        return Status.IN_PROGRESS;
    }

    /**
     * Determines if the specified position counts as a legal move.
     * A move is legal if the position contains {@link Piece#NONE},
     * {@link Piece#RED} or {@link Piece#YELLOW}.
     * @param move a {@link Position} of a move to be analyzed
     * @return {@code true} if the {@link Position} counts as a legal move,
     * otherwise {@code false}
     */
    @Override
    public boolean isLegalMove(final Position move) {
        if (!isOnTheBoard(move) || isGameOver()) {
            return false;
        }

        return switch (gameBoard[move.row()][move.col()].get()) {
            case NONE, YELLOW, RED -> true;
            case GREEN -> false;
        };
    }

    /**
     * Determines if a specified position is on the game board.
     * @param move the {@link Position} to be analyzed
     * @return {@code true} if the position is on the board,
     * otherwise {@code false}
     */
    private boolean isOnTheBoard(final Position move) {
        return move.row() >= 0 && move.col() >= 0
                && move.row() < BOARD_SIZE && move.col() < BOARD_SIZE;
    }

    /**
     * Makes a move with the {@link #nextPlayer} at the specified position.
     * @param move the {@link Position} of the move to be played
     * @throws IllegalArgumentException if an illegal move is played
     * @throws IllegalStateException if a legal move
     * was trying to move a {@link Piece#GREEN}
     */
    @Override
    public void makeMove(final Position move) {
        if (!isLegalMove(move)) {
            Logger.warn("An invalid move was made!");
            throw new IllegalArgumentException();
        }

        gameBoard[move.row()][move.col()].set(
                switch (getPiece(move.row(), move.col())) {
                    case NONE -> Piece.RED;
                    case RED -> Piece.YELLOW;
                    case YELLOW -> Piece.GREEN;
                    default -> throw new IllegalStateException();
                }
        );

        nextPlayer.set(nextPlayer.get().opponent());
        Logger.info("Made a move ({}, {}), which is now {}",
                move.row(),
                move.col(),
                getPiece(move.row(), move.col()));

        if (isGameOver()) {
            Logger.info("{} has won the game!", nextPlayer.get().opponent());
        }
    }

    /**
     * Gathers all the legal moves in the current position.
     * @return a {@link Set} of every legal move's position
     */
    @Override
    public Set<Position> getLegalMoves() {
        Set<Position> legalMoves = new HashSet<>();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Position move = new Position(i, j);
                if (isLegalMove(move)) {
                    legalMoves.add(move);
                }
            }
        }
        Logger.info("List of all legal moves: {}",  legalMoves);
        return legalMoves;
    }

    /**
     * Helper function for saving the game state,
     * which unpacks pieces from their wrappers.
     * @return a {@link Piece} array of arrays that represent the game board
     */
    public Piece[][] getBoardData() {
        Piece[][] boardData = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardData[i][j] =  gameBoard[i][j].get();
            }
        }
        return boardData;
    }

    /**
     * Helper function for loading a game state,
     * which wraps the pieces and the next player.
     * @param boardData a {@link Piece} array of arrays
     *                  that represent the game board
     * @param player the {@link Player} on the current turn
     */
    public void loadBoardData(final Piece[][] boardData, final Player player) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j].set(boardData[i][j]);
            }
        }
        nextPlayer.set(player);
    }

    /**
     * Creates a deep copy of this model.
     * @return the new copy of this model
     */
    @SuppressWarnings("unchecked")
    @Override
    public BoardGameModel copy() {
        BoardGameModel stateCopy = new BoardGameModel();
        stateCopy.gameBoard = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                stateCopy.gameBoard[i][j] =
                        new ReadOnlyObjectWrapper<>(gameBoard[i][j].get());
            }
        }
        stateCopy.nextPlayer = new ReadOnlyObjectWrapper<>(nextPlayer.get());
        Logger.info("Created deep copy of BoardGameModel");
        return stateCopy;
    }

    /**
     * Creates a string from the current state of the game board.
     * @return a string representing the game board
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                sb.append(switch (gameBoard[i][j].get()) {
                    case NONE -> "-";
                    case RED -> "R";
                    case YELLOW -> "Y";
                    case GREEN -> "G";
                });
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
