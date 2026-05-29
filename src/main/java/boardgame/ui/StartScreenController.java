package boardgame.ui;

import boardgame.model.FileManager;
import boardgame.model.GameResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxutils.JFXUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for the starting screen.
 * This is where player names are entered and previous results are displayed.
 */
public class StartScreenController {

    /**
     * Specifies how many of the previous game results are displayed.
     */
    private static final int NUM_RESULTS = 3;

    /**
     * The date display format of the results shown.
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    /**
     * Text Box for entering the name of the first player.
     */
    @FXML
    private TextField player1;

    /**
     * Text Box for entering the name of the second player.
     */
    @FXML
    private TextField player2;

    /**
     * Container for listing previous game results.
     */
    @FXML
    private VBox resultsBox;

    /**
     * Default constructor required by JavaFX controllers.
     */
    public StartScreenController() { }

    /**
     * Automatically loaded by JavaFX when the screen loads.
     * Loads and displays previous game results.
     */
    @FXML
    public void initialize() {
        try {
            ArrayList<GameResult> results = FileManager.loadAndGetResults();

            if (results.isEmpty()) {
                resultsBox.getChildren().add(
                        new Label("No previous games yet.")
                );
                return;
            }

            List<GameResult> recentGames = results.stream()
                    .sorted(Comparator.comparing(GameResult::date).reversed())
                    .limit(NUM_RESULTS).toList();

            for (GameResult result : recentGames) {
                String text = String.format("%s vs %s - Winner: %s - %s",
                        result.player1(),
                        result.player2(),
                        result.winner(),
                        result.date().format(DATE_TIME_FORMATTER)
                );

                resultsBox.getChildren().add(new Label(text));
            }

        } catch (IOException e) {
            Logger.error("Failed to load results: {}", e.getMessage());
        }
    }

    /**
     * Called when the Start Game button is pressed.
     * Loads the scene for the boardgame and transfers
     * the name of the players for the controller.
     *
     * @param event the event of the button press
     * @throws IOException if an error occurs while loading ui.fxml
     */
    @FXML
    private void onStartGame(final ActionEvent event) throws IOException {
        Stage stage = JFXUtils.getWindow((Node) event.getSource());
        JFXUtils.loadFXML(
                stage,
                StartScreenController.class,
                "/ui.fxml",
                this::setPlayerNames
        );
    }

    /**
     * Sets the name for both players in the specified controller instance.
     * Names are changed to "Player 1" and "Player 2" by default if they are
     * empty. If both names are the same, "2" is appended at the end of the
     * second player's name.
     * @param controller the specified controller instance
     */
    private void setPlayerNames(final BoardGameController controller) {
        String name1 = player1.getText();
        String name2 = player2.getText();

        if (name1.isEmpty()) {
            name1 = "Player 1";
        }

        if (name2.isEmpty()) {
            name2 = "Player 2";
        }

        if (name1.equals(name2)) {
            name2 = String.format("%s2", name1);
        }

        controller.setPlayer1Name(name1);
        controller.setPlayer2Name(name2);
    }
}
