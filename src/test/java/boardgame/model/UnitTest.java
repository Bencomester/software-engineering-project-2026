package boardgame.model;

import common.util.board.Position;
import game.State.Status;
import game.State.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    @Test
    void initialization() {
        BoardGameModel model = new BoardGameModel();

        assertFalse(model.isGameOver());
        assertEquals(Status.IN_PROGRESS, model.getStatus());
        assertEquals("---\n---\n---\n", model.toString());
        assertEquals(Player.PLAYER_1, model.getNextPlayer());
        assertEquals(9, model.getLegalMoves().size());
    }

    @Test
    void isLegalMove() {
        BoardGameModel model = new BoardGameModel();

        assertTrue(model.isLegalMove(new Position(0, 0)));
        assertTrue(model.isLegalMove(new Position(1, 1)));
        assertTrue(model.isLegalMove(new Position(2, 2)));

        assertFalse(model.isLegalMove(new Position(-1, 1)));
        assertFalse(model.isLegalMove(new Position(1, 3)));
        assertFalse(model.isLegalMove(new Position(3, 2)));
    }

    @Test
    void legalMoves() {
        BoardGameModel model = new BoardGameModel();
        Position nonePos = new Position(0, 0);
        Position redPos = new Position(1, 2);
        Position yellowPos = new Position(2, 1);
        Position greenPos = new Position(1, 1);

        assertDoesNotThrow(() -> {
            model.makeMove(redPos);
            model.makeMove(yellowPos);
            model.makeMove(yellowPos);
            model.makeMove(greenPos);
            model.makeMove(greenPos);
            model.makeMove(greenPos);
        });

        assertTrue(model.isLegalMove(nonePos));
        assertTrue(model.isLegalMove(redPos));
        assertTrue(model.isLegalMove(yellowPos));
        assertFalse(model.isLegalMove(greenPos));
    }

    @Test
    void makeMove() {
        BoardGameModel model = new BoardGameModel();
        Position pos = new Position(0, 0);

        model.makeMove(pos);
        assertEquals(Player.PLAYER_2, model.getNextPlayer());
        assertEquals("R--\n---\n---\n", model.toString());

        model.makeMove(pos);
        assertEquals(Player.PLAYER_1, model.getNextPlayer());
        assertEquals("Y--\n---\n---\n", model.toString());

        model.makeMove(pos);
        assertEquals(Player.PLAYER_2, model.getNextPlayer());
        assertEquals("G--\n---\n---\n", model.toString());

        assertThrows(IllegalArgumentException.class, () -> model.makeMove(pos));
    }

    @Test
    void gameOverWithRow() {
        BoardGameModel model = new BoardGameModel();
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(0, 1);
        Position pos3 = new Position(0, 2);

        model.makeMove(pos1);
        model.makeMove(pos2);
        model.makeMove(pos3);

        assertEquals(Status.PLAYER_1_WINS, model.getStatus());
        assertTrue(model.getLegalMoves().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> model.makeMove(pos1));
    }

    @Test
    void gameOverWithColumn() {
        BoardGameModel model = new BoardGameModel();
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(1, 0);
        Position pos3 = new Position(2, 0);

        model.makeMove(pos1);
        model.makeMove(pos2);
        model.makeMove(pos3);

        assertEquals(Status.PLAYER_1_WINS, model.getStatus());
        assertTrue(model.getLegalMoves().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> model.makeMove(pos1));
    }

    @Test
    void gameOverWithDiagonal1() {
        BoardGameModel model = new BoardGameModel();
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(1, 1);
        Position pos3 = new Position(2, 2);

        model.makeMove(pos1);
        model.makeMove(pos2);
        model.makeMove(pos3);

        assertEquals(Status.PLAYER_1_WINS, model.getStatus());
        assertTrue(model.getLegalMoves().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> model.makeMove(pos1));
    }

    @Test
    void gameOverWithDiagonal2() {
        BoardGameModel model = new BoardGameModel();
        Position pos1 = new Position(2, 0);
        Position pos2 = new Position(1, 1);
        Position pos3 = new Position(0, 2);

        model.makeMove(pos1);
        model.makeMove(pos2);
        model.makeMove(pos3);

        assertEquals(Status.PLAYER_1_WINS, model.getStatus());
        assertTrue(model.getLegalMoves().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> model.makeMove(pos1));
    }

    @Test
    void resetGameBoard() {
        BoardGameModel model = new BoardGameModel();
        Position pos1 = new Position(1, 1);

        model.makeMove(pos1);
        model.resetGameBoard();

        assertEquals(Player.PLAYER_1, model.getNextPlayer());
        assertEquals("---\n---\n---\n", model.toString());
    }

    @Test
    void saveAndLoadGameState(@TempDir Path tempDir) throws IOException {
        File saveFile = tempDir.resolve("temp_save.json").toFile();
        BoardGameModel model = new BoardGameModel();
        Position pos1 = new Position(0, 0);

        model.makeMove(pos1);
        FileManager.saveGameStateToFile(model, saveFile);
        model = new BoardGameModel();
        FileManager.loadGameStateFromFile(model, saveFile);

        assertTrue(saveFile.exists());
        assertTrue(saveFile.isFile());
        assertTrue(saveFile.length() > 0);
        assertEquals(Player.PLAYER_2, model.getNextPlayer());
        assertEquals("R--\n---\n---\n", model.toString());
    }
}
