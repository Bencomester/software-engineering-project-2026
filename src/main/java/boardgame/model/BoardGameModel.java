package boardgame.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.util.board.Position;
import game.State;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BoardGameModel implements State<Position, BoardGameModel> {

    private static final int BOARD_SIZE = 3;

    private ReadOnlyObjectWrapper<Piece>[][] gameBoard;
    private ReadOnlyObjectWrapper<Player> nextPlayer;

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

    public ReadOnlyObjectProperty<Player> getNextPlayerProperty() {
        return nextPlayer.getReadOnlyProperty();
    }

    @Override
    public Player getNextPlayer() {
        return nextPlayer.get();
    }

    public ReadOnlyObjectProperty<Piece> getPieceProperty(int i, int j) {
        return gameBoard[i][j].getReadOnlyProperty();
    }

    public Piece getPiece(int i, int j) {
        return gameBoard[i][j].get();
    }

    public void resetGameBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j].set(Piece.NONE);
            }
        }

        nextPlayer.set(Player.PLAYER_1);
        Logger.info("Game Board reset");
    }

    @Override
    public boolean isGameOver() {
        return checkRows() || checkColumns() || checkDiagonalFromTopLeft() || checkDiagonalsFromBottomLeft();
    }

    @SuppressWarnings("DuplicatedCode")
    private boolean checkRows() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean areRowPiecesSame = true;
            Piece piece = gameBoard[i][0].get();
            if (piece == Piece.NONE) continue;

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

    @SuppressWarnings("DuplicatedCode")
    private boolean checkColumns() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean areRowPiecesSame = true;
            Piece piece = gameBoard[0][i].get();
            if (piece == Piece.NONE) continue;

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

    private boolean checkDiagonalFromTopLeft() {
        Piece piece = gameBoard[0][0].get();
        if (piece == Piece.NONE) return false;

        for (int i = 1; i < BOARD_SIZE; i++) {
            if (piece != gameBoard[i][i].get()) return false;
        }
        return true;
    }

    private boolean checkDiagonalsFromBottomLeft() {
        Piece piece = gameBoard[BOARD_SIZE - 1][0].get();
        if (piece == Piece.NONE) return false;

        for (int i = 1; i < BOARD_SIZE; i++) {
            if (piece != gameBoard[BOARD_SIZE - i - 1][i].get()) return false;
        }
        return true;
    }

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

    @Override
    public boolean isLegalMove(Position move) {
        if (!isOnTheBoard(move) || isGameOver()) return false;

        return switch (gameBoard[move.row()][move.col()].get()) {
            case NONE, YELLOW, RED -> true;
            case GREEN -> false;
        };
    }

    private boolean isOnTheBoard(Position move) {
        return move.row() >= 0 && move.col() >= 0 && move.row() < BOARD_SIZE && move.col() < BOARD_SIZE;
    }

    @Override
    public void makeMove(Position move) {
        if (!isLegalMove(move)) {
            Logger.warn("An invalid move was made!");
            throw new IllegalArgumentException();
        }

        gameBoard[move.row()][move.col()].set(
                switch (getPiece(move.row(), move.col())) {
                    case NONE -> Piece.RED;
                    case RED -> Piece.YELLOW;
                    case YELLOW -> Piece.GREEN;
                    case GREEN -> throw new IllegalStateException();
                }
        );

        nextPlayer.set(nextPlayer.get().opponent());
        Logger.info(String.format("Made a move (%d, %d), which is now %s", move.row(), move.col(), getPiece(move.row(), move.col())));
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
        Logger.info(String.format("List of all legal moves: %s",  legalMoves));
        return legalMoves;
    }

    public void saveGameStateToFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GameSave save = new GameSave(getBoardData(), getNextPlayer());
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, save);
        Logger.info(String.format("Saved game state to file: %s", file.getAbsolutePath()));
    }

    public void loadGameStateFromFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GameSave save = mapper.readValue(file, GameSave.class);
        loadBoardData(save.board(), save.nextPlayer());
        Logger.info(String.format("Loaded game state from file: %s", file.getAbsolutePath()));
    }

    private Piece[][] getBoardData() {
        Piece[][] boardData = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardData[i][j] =  gameBoard[i][j].get();
            }
        }
        return boardData;
    }

    private void loadBoardData(Piece[][] boardData, Player player) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j].set(boardData[i][j]);
            }
        }
        nextPlayer.set(player);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BoardGameModel copy() {
        BoardGameModel stateCopy = new BoardGameModel();
        stateCopy.gameBoard = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                stateCopy.gameBoard[i][j] = new ReadOnlyObjectWrapper<>(gameBoard[i][j].get());
            }
        }
        stateCopy.nextPlayer = new ReadOnlyObjectWrapper<>(nextPlayer.get());
        Logger.info("Created deep copy of BoardGameModel");
        return stateCopy;
    }

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
