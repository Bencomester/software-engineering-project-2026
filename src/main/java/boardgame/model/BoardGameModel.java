package boardgame.model;

import common.util.board.Position;
import game.State;

import java.util.HashSet;
import java.util.Set;

public class BoardGameModel implements State<Position, BoardGameModel> {

    private static final int BOARD_SIZE = 3;

    private Piece[][] gameBoard;
    private Player nextPlayer;

    public BoardGameModel() {
        nextPlayer = Player.PLAYER_1;
        gameBoard = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j] = Piece.NONE;
            }
        }
    }

    @Override
    public Player getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public boolean isGameOver() {
        return checkRows() || checkColumns() || checkDiagonalFromTopLeft() || checkDiagonalsFromBottomLeft();
    }

    private boolean checkRows() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean areRowPiecesSame = true;
            Piece piece = gameBoard[i][0];
            if (piece == Piece.NONE) continue;

            for (int j = 1; j < BOARD_SIZE; j++) {
                if (piece != gameBoard[i][j]) {
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

    private boolean checkColumns() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean areRowPiecesSame = true;
            Piece piece = gameBoard[0][i];
            if (piece == Piece.NONE) continue;

            for (int j = 1; j < BOARD_SIZE; j++) {
                if (piece != gameBoard[j][i]) {
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

    private boolean checkDiagonalFromTopLeft() {
        Piece piece = gameBoard[0][0];
        if (piece == Piece.NONE) return false;

        for (int i = 1; i < BOARD_SIZE; i++) {
            if (piece != gameBoard[i][i]) return false;
        }
        return true;
    }

    private boolean checkDiagonalsFromBottomLeft() {
        Piece piece = gameBoard[BOARD_SIZE - 1][0];
        if (piece == Piece.NONE) return false;

        for (int i = 1; i < BOARD_SIZE; i++) {
            if (piece != gameBoard[BOARD_SIZE - i - 1][i]) return false;
        }
        return true;
    }

    @Override
    public Status getStatus() {
        if (isGameOver()) {
            return switch (nextPlayer) {
                case PLAYER_1 -> Status.PLAYER_2_WINS;
                case PLAYER_2 -> Status.PLAYER_1_WINS;
            };
        }

        return Status.IN_PROGRESS;
    }

    @Override
    public boolean isLegalMove(Position move) {
        if (!isOnTheBoard(move) || isGameOver()) return false;

        return switch (gameBoard[move.row()][move.col()]) {
            case NONE, YELLOW, RED -> true;
            case GREEN -> false;
        };
    }

    private boolean isOnTheBoard(Position move) {
        return move.row() >= 0 && move.col() >= 0 && move.row() < BOARD_SIZE && move.col() < BOARD_SIZE;
    }

    @Override
    public void makeMove(Position move) {
        if (!isLegalMove(move)) throw new IllegalArgumentException();

        nextPlayer = nextPlayer.opponent();
        gameBoard[move.row()][move.col()] = switch (gameBoard[move.row()][move.col()]) {
            case NONE -> Piece.RED;
            case RED -> Piece.YELLOW;
            case YELLOW -> Piece.GREEN;
            case GREEN -> throw new IllegalStateException();
        };
    }

    @Override
    public Set<Position> getLegalMoves() {
        Set<Position> legalMoves = new HashSet<>();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Position move = new Position(i, j);
                if (isLegalMove(move)) legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    @Override
    public BoardGameModel copy() {
        BoardGameModel stateCopy = new BoardGameModel();
        stateCopy.gameBoard = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            stateCopy.gameBoard[i] = this.gameBoard[i].clone();
        }
        stateCopy.nextPlayer = nextPlayer;

        return stateCopy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                sb.append(switch (gameBoard[i][j]) {
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
