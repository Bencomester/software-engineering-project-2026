package boardgame.model;

import common.util.board.Position;
import game.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileManagerTest {

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
        assertEquals(State.Player.PLAYER_2, model.getNextPlayer());
        assertEquals("R--\n---\n---\n", model.toString());
    }
}
