package kairo;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles interactions between Kairo and the user.
 */
public class Ui {
    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Kairo's welcome message.
     */
    public void showWelcome() {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("Hello! I'm Kairo.");
        System.out.println("What can I do for you?");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Returns whether another command is available.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     *
     * @return trimmed user command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays all tasks.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(HORIZONTAL_LINE);

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }

        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task task that was marked
     */
    public void showMarked(Task task) {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked
     */
    public void showUnmarked(Task task) {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(task);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task task that was deleted
     * @param taskCount number of remaining tasks
     */
    public void showDeleted(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Noted. I've removed this task:");
        System.out.println(task);
        System.out.println(
                "Now you have " + taskCount + " "
                        + taskWord + " in the list.");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount number of tasks currently stored
     */
    public void showTaskAdded(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println(
                "Now you have " + taskCount + " "
                        + taskWord + " in the list.");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays an error message.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("OOPS! " + message);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays Kairo's goodbye message.
     */
    public void showGoodbye() {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }
}