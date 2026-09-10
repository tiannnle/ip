package kairo;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Displays Kairo's chat window and forwards commands to the application.
 */
public class Main extends Application {

    private final Kairo kairo = new Kairo();
    private VBox dialogContainer;
    private ScrollPane scrollPane;
    private TextField userInput;
    private Button sendButton;

    /**
     * Creates the JavaFX application.
     */
    public Main() {
    }

    /**
     * Builds and displays the chat window.
     *
     * @param stage Primary application window.
     */
    @Override
    public void start(Stage stage) {
        Label title = new Label("Kairo");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Your tasks, one message at a time.");
        subtitle.getStyleClass().add("subtitle");
        VBox header = new VBox(4, title, subtitle);
        header.getStyleClass().add("header");

        dialogContainer = new VBox(12);
        dialogContainer.setId("dialogContainer");
        dialogContainer.getStyleClass().add("dialog-container");
        scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogContainer.heightProperty().addListener((observable) ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));

        userInput = new TextField();
        userInput.setId("userInput");
        userInput.setPromptText("Type a command, e.g. todo read book");
        userInput.setAccessibleText("Command");
        userInput.setOnAction(event -> handleUserInput());

        sendButton = new Button("Send");
        sendButton.setId("sendButton");
        sendButton.setPrefWidth(80);
        sendButton.setOnAction(event -> handleUserInput());

        HBox inputRow = new HBox(10, userInput, sendButton);
        HBox.setHgrow(userInput, Priority.ALWAYS);
        Label hint = new Label(
                "list · todo · deadline · event · find · mark · unmark · delete · bye");
        hint.getStyleClass().add("command-hint");
        hint.setWrapText(true);
        VBox footer = new VBox(8, inputRow, hint);
        footer.getStyleClass().add("footer");

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(scrollPane);
        root.setBottom(footer);
        Scene scene = new Scene(root, 620, 720);
        scene.getStylesheets().add(Objects.requireNonNull(
                Main.class.getResource("/styles/kairo.css")).toExternalForm());

        dialogContainer.getChildren().add(
                new DialogBox(kairo.getWelcomeMessage(), false));
        stage.setTitle("Kairo");
        stage.setMinWidth(420);
        stage.setMinHeight(480);
        stage.setScene(scene);
        stage.show();
        userInput.requestFocus();
    }

    /**
     * Displays the user's command and Kairo's response, then clears the input.
     */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || kairo.isExit()) {
            return;
        }
        String response = kairo.getResponse(input);
        dialogContainer.getChildren().addAll(
                new DialogBox(input, true),
                new DialogBox(response, false));
        userInput.clear();

        if (kairo.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            // Allow the farewell to appear before closing the window.
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        } else {
            userInput.requestFocus();
        }
    }
}
