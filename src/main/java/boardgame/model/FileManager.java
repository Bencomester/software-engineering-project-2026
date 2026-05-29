package boardgame.model;

import boardgame.ui.BoardGameController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Utility class for the saving and loading of game states and results.
 */
public final class FileManager {

    /**
     * File where the results of the previous games are saved.
     */
    private static final File RESULTS_FILE =
            Paths.get(System.getProperty("user.home"))
                    .resolve(".ttt_bb_results.json").toFile();

    /**
     * Object mapper with java time module and without timestamp serialization.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private FileManager() { }

    /**
     * Saves the specified game result to {@link #RESULTS_FILE}.
     * @param result the specified result to be saved
     * @throws IOException if there is an error while writing the file
     */
    public static void saveResult(final GameResult result) throws IOException {
        ArrayList<GameResult> gameResults = loadAndGetResults();
        gameResults.add(result);

        OBJECT_MAPPER
                .writerWithDefaultPrettyPrinter()
                .writeValue(RESULTS_FILE, gameResults);
        Logger.info("Saved new game result to: {}",
                RESULTS_FILE.getAbsolutePath());
    }

    /**
     * Loads all game results from {@link #RESULTS_FILE}
     * and returns them in a list.
     * @return array list of previous game results
     * @throws IOException if there is an error while reading the file
     */
    public static ArrayList<GameResult> loadAndGetResults() throws IOException {
        if (!RESULTS_FILE.exists()) {
            return new ArrayList<>();
        }

        Logger.info("Loading game results from: {}",
                RESULTS_FILE.getAbsolutePath());

        return OBJECT_MAPPER.readValue(
                RESULTS_FILE,
                OBJECT_MAPPER.getTypeFactory()
                        .constructCollectionType(
                                ArrayList.class,
                                GameResult.class
                        )
        );
    }

    /**
     * Saves the current game state of the model to a specified file.
     * @param model the model for acquiring the game state
     * @param file the file in which to save
     * @throws IOException if an IO write error of some sort occurs
     */
    public static void saveGameStateToFile(
            final BoardGameModel model,
            final File file) throws IOException {
        saveGameStateToFile(model, file, "Player 1", "Player 2");
    }

    /**
     * Saves the current game state of the model to a specified file.
     * @param model the model for acquiring the game state
     * @param file the file in which to save
     * @param name1 the name of the first player
     * @param name2 the name of the second player
     * @throws IOException if an IO write error of some sort occurs
     */
    public static void saveGameStateToFile(
            final BoardGameModel model,
            final File file,
            final String name1, final String name2) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GameSave save = new GameSave(
                model.getBoardData(),
                model.getNextPlayer(),
                name1,
                name2
        );
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, save);
        Logger.info("Saved game state to file: {}", file.getAbsolutePath());
    }

    /**
     * Loads a game state to the model from a specified file,
     * while loading the player names in the controller.
     * @param model the model to where the game state is loaded
     * @param file the file from where to load a previous save
     * @param controller the controller for loading the player names
     * @throws IOException if an IO read error of some sort occurs
     */
    public static void loadGameStateFromFileWithPlayerNames(
            final BoardGameModel model,
            final File file,
            final BoardGameController controller) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GameSave save = mapper.readValue(file, GameSave.class);
        model.loadBoardData(save.board(), save.nextPlayer());
        controller.setPlayer1Name(save.player1name());
        controller.setPlayer2Name(save.player2name());
        Logger.info("Loaded game state from file: {}", file.getAbsolutePath());
    }

    /**
     * Loads a game state to the model from a specified file.
     * @param model the model to where the game state is loaded
     * @param file the file from where to load a previous save
     * @throws IOException if an IO read error of some sort occurs
     */
    public static void loadGameStateFromFile(
            final BoardGameModel model,
            final File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GameSave save = mapper.readValue(file, GameSave.class);
        model.loadBoardData(save.board(), save.nextPlayer());
        Logger.info("Loaded game state from file: {}", file.getAbsolutePath());
    }
}
