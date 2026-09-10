package kairo;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Displays a chat message with its sender and appropriate alignment.
 */
public class DialogBox extends HBox {

    /**
     * Creates a message bubble that wraps as the window changes size.
     *
     * @param message Text to display.
     * @param isUser True for a message sent by the user.
     */
    public DialogBox(String message, boolean isUser) {
        Label sender = new Label(isUser ? "You" : "Kairo");
        sender.getStyleClass().add("sender-label");

        Label text = new Label(message);
        text.getStyleClass().add("message-text");
        text.setWrapText(true);
        text.setMinWidth(0);
        text.setMinHeight(Region.USE_PREF_SIZE);

        VBox bubble = new VBox(5, sender, text);
        bubble.getStyleClass().add("bubble");
        bubble.setMinWidth(0);
        bubble.maxWidthProperty().bind(widthProperty().multiply(0.85));

        getStyleClass().add(isUser ? "user-dialog" : "kairo-dialog");
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getChildren().add(bubble);
    }
}
