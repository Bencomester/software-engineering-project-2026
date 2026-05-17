package boardgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Objects;

public final class BoardGameApplication extends Application {

    @Override
    public void start(final Stage stage) throws IOException {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/ui.fxml"))
        );
        stage.setTitle("Tic-Tac-Toe but better");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Logger.info("Application started and scene loaded");
    }
}
