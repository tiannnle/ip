package kairo;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Displays compact user commands, wider Kairo replies, and clearly labeled errors.
 */
public class DialogBox extends HBox {

    /**
     * Creates a message bubble that wraps as the window changes size.
     *
     * @param message Text to display.
     * @param isUser True for a message sent by the user.
     */
    public DialogBox(String message, boolean isUser) {
        boolean isError = !isUser && isErrorMessage(message);
        String senderName = isUser ? "You" : "Kairo";
        if (isError) {
            senderName = "Kairo · Error";
        }
        Label sender = new Label(senderName);
        sender.getStyleClass().add("sender-label");

        Label text = new Label(message);
        text.getStyleClass().add("message-text");
        text.setWrapText(true);
        text.setMinWidth(0);
        text.setMinHeight(Region.USE_PREF_SIZE);
        text.setMaxWidth(Double.MAX_VALUE);

        VBox bubble = new VBox(5, sender, text);
        bubble.getStyleClass().add("bubble");
        bubble.setMinWidth(0);
        if (isUser) {
            bubble.maxWidthProperty().bind(widthProperty().multiply(0.76));
        } else {
            bubble.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(bubble, Priority.ALWAYS);
        }

        getStyleClass().add(isUser ? "user-dialog" : "kairo-dialog");
        if (isError) {
            getStyleClass().add("error-dialog");
        }
        setMinWidth(0);
        setMinHeight(Region.USE_PREF_SIZE);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getChildren().add(bubble);
    }

    /**
     * Recognizes command errors and errors appended to the startup greeting.
     *
     * @param message Response supplied by Kairo.
     * @return True when the response contains Kairo's error prefix.
     */
    static boolean isErrorMessage(String message) {
        return message.startsWith("OOPS!") || message.contains("\n\nOOPS!");
    }
}
