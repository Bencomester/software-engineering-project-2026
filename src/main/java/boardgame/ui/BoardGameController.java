package boardgame.ui;

import boardgame.model.BoardGameModel;
import boardgame.model.Piece;
import common.util.board.Position;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import jfxutils.images.EnumImageStorage;
import jfxutils.images.ImageStorage;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;

public class BoardGameController {

    @FXML
    private GridPane board;

    @FXML
    private Label turnLabel;

    private final BoardGameModel model = new BoardGameModel();

    private final ImageStorage<Piece> imageStorage = new EnumImageStorage<>(Piece.class);

    @FXML
    public void initialize() {
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                Position pos = new Position(row, col);
                StackPane piece = createPiece(pos);
                board.add(piece, col, row);
            }
        }

        createLabelBind();
        Logger.info("Controller has been initialized");
    }

    private StackPane createPiece(Position pos) {
        StackPane piece = new StackPane();
        piece.getStyleClass().add("piece");
        ImageView imageView = createImageView(pos);
        piece.getChildren().add(imageView);
        piece.setOnMouseClicked(this::handleMouseClick);
        return piece;
    }

    private ImageView createImageView(Position pos) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.imageProperty().bind(
                new ObjectBinding<>() {
                    {
                        super.bind(model.getPieceProperty(pos.row(), pos.col()));
                    }
                    @Override
                    protected Image computeValue() {
                    return imageStorage.get(model.getPieceProperty(pos.row(), pos.col()).get()).orElse(null);
                }
        });
        return imageView;
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        StackPane piece = (StackPane) event.getSource();
        int row = GridPane.getRowIndex(piece);
        int col = GridPane.getColumnIndex(piece);
        Position pos = new Position(row, col);
        Logger.info(String.format("Click registered: %s", pos));
        if (model.isLegalMove(pos)) {
            model.makeMove(pos);
        }
    }

    @FXML
    private void onQuit() {
        Logger.info("Quitting application");
        Platform.exit();
    }

    @FXML
    private void onReset() {
        model.resetGameBoard();
    }

    @FXML
    private void onSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));

        File file = fileChooser.showSaveDialog(board.getScene().getWindow());
        if (file == null) {
            Logger.warn("No file selected, saving canceled");
            return;
        }

        try {
            model.saveGameStateToFile(file);
        } catch (IOException e) {
            Logger.error(String.format("Couldn't save game: %s", e.getMessage()));
            showError("Failed to save game", e);
        }
    }

    @FXML
    private void onLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));

        File file = fileChooser.showOpenDialog(board.getScene().getWindow());
        if (file == null) {
            Logger.warn("No file selected, loading canceled");
            return;
        }

        try {
            model.loadGameStateFromFile(file);
        }  catch (IOException e) {
            Logger.error(String.format("Couldn't load game: %s", e.getMessage()));
            showError("Failed to load game", e);
        }
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Tic-Tac-Toe but better");
        alert.setContentText("""
                Created By: %s
                Java vendor: %s
                Java version: %s
                JavaFX version: %s
                """.formatted(System.getProperty("user.name"), System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("javafx.version")));

        Logger.info("About was shown");
        alert.showAndWait();
    }

    private void createLabelBind() {
        turnLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            if (model.isGameOver()) {
                                return String.format("Winner: %s!", model.getNextPlayer().opponent());
                            } else {
                                return String.format("Turn: %s", model.getNextPlayer());
                            }
                        }, model.getNextPlayerProperty()
                )
        );
    }
}
