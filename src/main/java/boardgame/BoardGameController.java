package boardgame;

import boardgame.model.BoardGameModel;
import boardgame.model.Piece;
import common.util.board.Position;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import jfxutils.images.EnumImageStorage;
import jfxutils.images.ImageStorage;

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
        if (model.isLegalMove(pos)) {
            model.makeMove(pos);
        }
    }

    @FXML
    private void onQuit() {
        Platform.exit();
    }

    @FXML
    private void onReset() {
        model.resetGameBoard();
    }

    @FXML
    private void onSave() {

    }

    @FXML
    private void onLoad() {

    }

    @FXML
    private void onAbout() {

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
