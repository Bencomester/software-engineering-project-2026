package boardgame.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Objects;

public class StartScreenController {

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
