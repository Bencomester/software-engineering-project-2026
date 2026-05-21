package boardgame.ui;

import boardgame.model.BoardGameModel;
import boardgame.model.Piece;
import common.util.board.Position;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

    /**
     * Height and width of the images that represent the pieces.
     */
    private static final int IMAGE_SIZE = 120;

    /**
     * Holds the GridPane UI element which is the game board.
     */
    @FXML
    private GridPane board;

    /**
     * Holds the Label element that displays turn and win status.
     */
    @FXML
    private Label turnLabel;

    /**
     * The model instance of the board game.
     */
    private final BoardGameModel model = new BoardGameModel();


    /**
     * An image storage made from {@link Piece} that holds colored rocks.
     */
    private final ImageStorage<Piece> imageStorage =
            new EnumImageStorage<>(Piece.class);

    /**
     * Property wrapping the name of the first player.
     */
    private final SimpleStringProperty player1Name = new SimpleStringProperty();

    /**
     * Property wrapping the name of the second player.
     */
    private final SimpleStringProperty player2Name = new SimpleStringProperty();

    /**
     * Sets name of the first player to the specified string.
     * @param player1 the specified string for the name
     */
    public void setPlayer1Name(final String player1) {
        this.player1Name.set(player1);
    }

    /**
     * Sets name of the second player to the specified string.
     * @param player2 the specified string for the name
     */
    public void setPlayer2Name(final String player2) {
        this.player2Name.set(player2);
    }

    /**
     * Returns the name of the player on the current turn.
     * @return the name of the player
     */
    private String getNextPlayerName() {
        return switch (model.getNextPlayer()) {
            case PLAYER_1 -> player1Name.get();
            case PLAYER_2 -> player2Name.get();
        };
    }

    /**
     * Returns the opponent name of the current player on turn.
     * @return the name of the player
     */
    private String getNextPlayerOpponentName() {
        return switch (model.getNextPlayer()) {
            case PLAYER_1 -> player2Name.get();
            case PLAYER_2 -> player1Name.get();
        };
    }

    /**
     * Called once when the controller is loaded.
     */
    @FXML
    public void initialize() {
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                Position pos = new Position(row, col);
                StackPane piece = createPiece(pos);
                board.add(piece, col, row);
            }
        }

        board.setGridLinesVisible(true);
        createLabelBind();
        Logger.info("Controller has been initialized");
    }

    /**
     * Creates a {@link StackPane} at the specified position
     * which serves as a game piece.
     * @param pos the {@link Position} which will identify the piece type
     * @return the newly created stack pane
     */
    private StackPane createPiece(final Position pos) {
        StackPane piece = new StackPane();
        piece.getStyleClass().add("piece");
        ImageView imageView = createImageView(pos);
        piece.getChildren().add(imageView);
        piece.setOnMouseClicked(this::handleMouseClick);
        return piece;
    }

    /**
     * Creates an image view for a stack pane, with the appropriate image.
     * @param pos the {@link Position} which will identify
     *            the piece type and thus the image
     * @return the newly created image view
     */
    private ImageView createImageView(final Position pos) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        ReadOnlyObjectProperty<Piece> pieceProperty
                = model.getPieceProperty(pos.row(), pos.col());
        imageView.imageProperty().bind(
                new ObjectBinding<>() {
                    {
                        super.bind(pieceProperty);
                    }
                    @Override
                    protected Image computeValue() {
                    return imageStorage
                            .get(pieceProperty.get()).orElse(null);
                }
        });
        return imageView;
    }

    /**
     * Called when a piece (stack pane) is clicked on the UI.
     * @param event contains information about the mouse click
     */
    @FXML
    private void handleMouseClick(final MouseEvent event) {
        StackPane piece = (StackPane) event.getSource();
        int row = GridPane.getRowIndex(piece);
        int col = GridPane.getColumnIndex(piece);
        Position pos = new Position(row, col);
        Logger.info("Click registered: {}", pos);
        if (model.isLegalMove(pos)) {
            model.makeMove(pos);
        }
    }

    /**
     * Called when the user clicks the Quit button.
     */
    @FXML
    private void onQuit() {
        Logger.info("Quitting application");
        Platform.exit();
    }

    /**
     * Called when the user clicks the Reset button.
     */
    @FXML
    private void onReset() {
        model.resetGameBoard();
    }

    /**
     * Called when the user clicks the Save button.
     * Creates and shows a file chooser save dialog
     * where the user can select the save file location.
     */
    @FXML
    private void onSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );

        File file = fileChooser.showSaveDialog(board.getScene().getWindow());
        if (file == null) {
            Logger.warn("No file selected, saving canceled");
            return;
        }

        try {
            model.saveGameStateToFile(file);
        } catch (IOException e) {
            Logger.error("Couldn't save game: {}", e.getMessage());
            showError("Failed to save game", e);
        }
    }

    /**
     * Called when the user clicks the Load button.
     * Creates and shows a file chooser load dialog
     * where the user can select the save file to be loaded.
     */
    @FXML
    private void onLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );

        File file = fileChooser.showOpenDialog(board.getScene().getWindow());
        if (file == null) {
            Logger.warn("No file selected, loading canceled");
            return;
        }

        try {
            model.loadGameStateFromFile(file);
        }  catch (IOException e) {
            Logger.error("Couldn't load game: {}", e.getMessage());
            showError("Failed to load game", e);
        }
    }

    /**
     * Creates an error type alert pop-up
     * with a custom and an exception message.
     * @param message the custom message shown as a header text
     * @param e the event which message is shown as content text
     */
    private void showError(final String message, final Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    /**
     * Called when the user click on the About button.
     * Creates and shows information type alert with details.
     */
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
                """.formatted(
                        System.getProperty("user.name"),
                        System.getProperty("java.vendor"),
                        System.getProperty("java.version"),
                        System.getProperty("javafx.version")
                )
        );

        Logger.info("About was shown");
        alert.showAndWait();
    }

    /**
     * Creates a bind between {@link #turnLabel} and
     * {@link BoardGameModel#getNextPlayerProperty()} using player names.
     */
    private void createLabelBind() {
        turnLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            if (model.isGameOver()) {
                                return String.format("Winner: %s!",
                                        getNextPlayerOpponentName()
                                );
                            } else {
                                return String.format("Turn: %s",
                                        getNextPlayerName()
                                );
                            }
                        },
                        model.getNextPlayerProperty(),
                        player1Name,
                        player2Name
                )
        );
    }
}
