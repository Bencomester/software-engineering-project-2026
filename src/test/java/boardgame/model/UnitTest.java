package boardgame.model;

import common.util.board.Position;
import game.State.Status;
import game.State.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    @Test
    void initialization() {
        BoardGameModel state = new BoardGameModel();

        assertFalse(state.isGameOver());
        assertEquals(Status.IN_PROGRESS, state.getStatus());
        assertEquals("---\n---\n---\n", state.toString());
        assertEquals(Player.PLAYER_1, state.getNextPlayer());
        assertEquals(9, state.getLegalMoves().size());
    }

    @Test
    void isLegalMove() {
        BoardGameModel state = new BoardGameModel();

        assertTrue(state.isLegalMove(new Position(0, 0)));
        assertTrue(state.isLegalMove(new Position(1, 1)));
        assertTrue(state.isLegalMove(new Position(2, 2)));

        assertFalse(state.isLegalMove(new Position(-1, 1)));
        assertFalse(state.isLegalMove(new Position(1, 3)));
        assertFalse(state.isLegalMove(new Position(3, 2)));
    }

    @Test
    void legalMoves() {
        BoardGameModel state = new BoardGameModel();
        Position nonePos = new Position(0, 0);
        Position redPos = new Position(1, 2);
        Position yellowPos = new Position(2, 1);
        Position greenPos = new Position(1, 1);

        assertDoesNotThrow(() -> {
            state.makeMove(redPos);
            state.makeMove(yellowPos);
            state.makeMove(yellowPos);
            state.makeMove(greenPos);
            state.makeMove(greenPos);
            state.makeMove(greenPos);
        });

        assertTrue(state.isLegalMove(nonePos));
        assertTrue(state.isLegalMove(redPos));
        assertTrue(state.isLegalMove(yellowPos));
        assertFalse(state.isLegalMove(greenPos));
    }

    @Test
    void makeMove() {
        BoardGameModel state = new BoardGameModel();
        Position pos = new Position(0, 0);

        state.makeMove(pos);
        assertEquals(Player.PLAYER_2, state.getNextPlayer());
        assertEquals("R--\n---\n---\n", state.toString());

        state.makeMove(pos);
        assertEquals(Player.PLAYER_1, state.getNextPlayer());
        assertEquals("Y--\n---\n---\n", state.toString());

        state.makeMove(pos);
        assertEquals(Player.PLAYER_2, state.getNextPlayer());
        assertEquals("G--\n---\n---\n", state.toString());

        assertThrows(IllegalArgumentException.class, () -> state.makeMove(pos));
    }

    @Test
    void gameOverWithRow() {
        BoardGameModel state = new BoardGameModel();
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(0, 1);
        Position pos3 = new Position(0, 2);

        state.makeMove(pos1);
        state.makeMove(pos2);
        state.makeMove(pos3);

        assertEquals(Status.PLAYER_1_WINS, state.getStatus());
        assertTrue(state.getLegalMoves().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> state.makeMove(pos1));
    }

    @Test
    void gameOverWithColumn() {
        BoardGameModel state = new BoardGameModel();
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(1, 0);
        Position pos3 = new Position(2, 0);

        state.makeMove(pos1);
        state.makeMove(pos2);
        state.makeMove(pos3);

        assertEquals(Status.PLAYER_1_WINS, state.getStatus());
        assertTrue(state.getLegalMoves().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> state.makeMove(pos1));
    }

    @Test
    void gameOverWithDiagonal1() {
        BoardGameModel state = new BoardGameModel();
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(1, 1);
        Position pos3 = new Position(2, 2);

        state.makeMove(pos1);
        state.makeMove(pos2);
        state.makeMove(pos3);

        assertEquals(Status.PLAYER_1_WINS, state.getStatus());
        assertTrue(state.getLegalMoves().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> state.makeMove(pos1));
    }

    @Test
    void gameOverWithDiagonal2() {
        BoardGameModel state = new BoardGameModel();
        Position pos1 = new Position(2, 0);
        Position pos2 = new Position(1, 1);
        Position pos3 = new Position(0, 2);

        state.makeMove(pos1);
        state.makeMove(pos2);
        state.makeMove(pos3);

        assertEquals(Status.PLAYER_1_WINS, state.getStatus());
        assertTrue(state.getLegalMoves().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> state.makeMove(pos1));
    }
}
