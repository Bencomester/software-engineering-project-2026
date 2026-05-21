package boardgame.ui;

import boardgame.model.FileManager;
import boardgame.model.GameResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
            FileManager.loadResults();
            ArrayList<GameResult> results = FileManager.getGameResults();

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
                        result.date()
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

        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/ui.fxml"))
        );
        Parent root = loader.load();

        BoardGameController controller = loader.getController();
        controller.setPlayer1Name(name1);
        controller.setPlayer2Name(name2);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

        Logger.info("Game started with players: {} and {}", name1, name2);
    }
}
