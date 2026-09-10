package kairo;

import javafx.application.Application;

/**
 * Launches JavaFX from a class that does not extend Application.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Starts the graphical interface.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
