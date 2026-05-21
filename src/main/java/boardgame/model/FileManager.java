package boardgame.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class FileManager {

    /**
     * File where the results of the previous games are saved.
     */
    private static final File RESULTS_FILE =
            Paths.get(System.getProperty("user.home"))
                    .resolve(".ttt_bb_results.json").toFile();

    /**
     * Contains all previous game results.
     */
    private static ArrayList<GameResult> gameResults = new ArrayList<>();

    private FileManager() { }

    /**
     * Saves the specified game result to {@link #RESULTS_FILE}.
     * @param result the specified result to be saved
     * @throws IOException if there is an error while writing the file
     */
    public static void saveResult(final GameResult result) throws IOException {
        loadResults();
        gameResults.add(result);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(RESULTS_FILE, gameResults);
        Logger.info("Saved new game result to: {}",
                RESULTS_FILE.getAbsolutePath());
    }

    /**
     * Loads all game results from {@link #RESULTS_FILE}
     * to {@link #gameResults}.
     * @throws IOException if there is an error while reading the file
     */
    public static void loadResults() throws IOException {
        if (!RESULTS_FILE.exists()) {
            gameResults = new ArrayList<>();
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        gameResults = mapper.readValue(
                RESULTS_FILE,
                mapper.getTypeFactory()
                        .constructCollectionType(
                                ArrayList.class,
                                GameResult.class
                        )
        );

        Logger.info("Loaded game results from: {}",
                RESULTS_FILE.getAbsolutePath());
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
        ObjectMapper mapper = new ObjectMapper();
        GameSave save = new GameSave(
                model.getBoardData(),
                model.getNextPlayer()
        );
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, save);
        Logger.info("Saved game state to file: {}", file.getAbsolutePath());
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
