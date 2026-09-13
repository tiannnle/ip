package kairo;

import java.time.LocalDate;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Displays Kairo's chat window and forwards commands to the application.
 */
public class Main extends Application {

    private static final PseudoClass INVALID_INPUT = PseudoClass.getPseudoClass("invalid-input");

    private final Kairo kairo = new Kairo();
    private VBox dialogContainer;
    private ScrollPane scrollPane;
    private TextField userInput;

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
        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createConversation());
        root.setBottom(createFooter());

        Scene scene = new Scene(root, 660, 760);
        scene.getStylesheets().add(Objects.requireNonNull(
                Main.class.getResource("/styles/kairo.css")).toExternalForm());
        dialogContainer.getChildren().add(new DialogBox(kairo.getWelcomeMessage(), false));

        stage.setTitle("Kairo");
        stage.setMinWidth(420);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
        userInput.requestFocus();
        scrollToLatest();
    }

    /**
     * Creates the product heading and command examples menu.
     *
     * @return Header that fits the available window width.
     */
    private HBox createHeader() {
        Label title = new Label("Kairo");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Your tasks, one message at a time.");
        subtitle.getStyleClass().add("subtitle");
        subtitle.setWrapText(true);
        subtitle.setMinWidth(0);
        VBox heading = new VBox(4, title, subtitle);
        heading.setMinWidth(0);
        HBox.setHgrow(heading, Priority.ALWAYS);

        HBox header = new HBox(16, heading, createCommandMenu());
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        return header;
    }

    /**
     * Creates a scrolling conversation that wraps messages as the window resizes.
     *
     * @return Scrollable conversation area.
     */
    private ScrollPane createConversation() {
        dialogContainer = new VBox(14);
        dialogContainer.setId("dialogContainer");
        dialogContainer.getStyleClass().add("dialog-container");
        dialogContainer.setMinWidth(0);
        dialogContainer.setFillWidth(true);
        scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setId("conversationScroll");
        scrollPane.setAccessibleText("Conversation with Kairo");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMinSize(0, 0);
        return scrollPane;
    }

    /**
     * Creates the command input with keyboard support and an empty-input guard.
     *
     * @return Input controls and their usage hint.
     */
    private VBox createFooter() {
        userInput = new TextField();
        userInput.setId("userInput");
        userInput.setPromptText("Type a command, e.g. todo read book");
        userInput.setAccessibleText("Command");
        userInput.setAccessibleHelp("Enter a command and press Enter, or choose an example from Commands.");
        userInput.setMinWidth(0);
        userInput.setOnAction(event -> handleUserInput());
        userInput.textProperty().addListener((observable, previous, current) ->
                userInput.pseudoClassStateChanged(INVALID_INPUT, false));

        Button sendButton = new Button("Send");
        sendButton.setId("sendButton");
        sendButton.getStyleClass().add("send-button");
        sendButton.setMinWidth(80);
        sendButton.setTooltip(new Tooltip("Send command (Enter)"));
        sendButton.setOnAction(event -> handleUserInput());
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> userInput.getText().isBlank() || userInput.isDisabled(),
                userInput.textProperty(), userInput.disabledProperty()));

        HBox inputRow = new HBox(10, userInput, sendButton);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(userInput, Priority.ALWAYS);
        Label hint = new Label("Enter to send · Open Commands for examples");
        hint.getStyleClass().add("command-hint");
        hint.setWrapText(true);
        VBox footer = new VBox(8, inputRow, hint);
        footer.getStyleClass().add("footer");
        return footer;
    }

    /**
     * Builds examples that users can edit before sending.
     *
     * @return Menu covering every supported command.
     */
    private MenuButton createCommandMenu() {
        MenuButton menu = new MenuButton("Commands");
        menu.setId("commandMenu");
        menu.getStyleClass().add("commands-button");
        menu.setMinWidth(Region.USE_PREF_SIZE);
        menu.setAccessibleText("Command examples");
        menu.setTooltip(new Tooltip("Choose an example, edit it, then press Enter."));
        String tomorrow = LocalDate.now().plusDays(1).toString();
        addCommandExample(menu, "Show all tasks", "list");
        addCommandExample(menu, "Add a todo", "todo read book");
        addCommandExample(menu, "Add a deadline", "deadline submit report /by " + tomorrow);
        addCommandExample(menu, "Add an event", "event team meeting /from " + tomorrow + " /to " + tomorrow);
        addCommandExample(menu, "Find tasks", "find book");
        addCommandExample(menu, "Mark a task", "mark 1");
        addCommandExample(menu, "Unmark a task", "unmark 1");
        addCommandExample(menu, "Delete a task", "delete 1");
        addCommandExample(menu, "Sort by name", "sort name");
        addCommandExample(menu, "Sort by date", "sort date");
        addCommandExample(menu, "Exit Kairo", "bye");
        return menu;
    }

    /**
     * Adds a menu item that fills the input without executing the command.
     *
     * @param menu Menu receiving the example.
     * @param label Description shown in the menu.
     * @param example Command to place in the input.
     */
    private void addCommandExample(MenuButton menu, String label, String example) {
        MenuItem item = new MenuItem(label);
        item.setOnAction(event -> {
            userInput.setText(example);
            userInput.requestFocus();
            userInput.positionCaret(example.length());
        });
        menu.getItems().add(item);
    }

    /**
     * Displays a command and its response, retaining failed input for correction.
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
        if (DialogBox.isErrorMessage(response)) {
            userInput.pseudoClassStateChanged(INVALID_INPUT, true);
        } else {
            userInput.clear();
        }
        scrollToLatest();

        if (kairo.isExit()) {
            userInput.setDisable(true);
            // Allow the farewell to appear before closing the window.
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        } else {
            userInput.requestFocus();
        }
    }

    /**
     * Scrolls after new messages are laid out, without reacting to window resizing.
     */
    private void scrollToLatest() {
        Platform.runLater(() -> {
            dialogContainer.applyCss();
            dialogContainer.layout();
            scrollPane.setVvalue(1.0);
        });
    }
}
