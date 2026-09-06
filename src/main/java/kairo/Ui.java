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
     * Checks whether another command is available.
     *
     * @return True if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command.
     *
     * @return Trimmed command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
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
     * Displays all stored tasks.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(HORIZONTAL_LINE);

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }

        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays tasks matching a search keyword.
     *
     * @param matchingTasks Matching tasks to display.
     */
    public void showMatchingTasks(ArrayList<Task> matchingTasks) {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("Here are the matching tasks in your list:");

        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println((i + 1) + "." + matchingTasks.get(i));
        }

        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays confirmation that a task was marked.
     *
     * @param task Marked task.
     */
    public void showMarked(Task task) {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays confirmation that a task was unmarked.
     *
     * @param task Unmarked task.
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
     * @param task Deleted task.
     * @param taskCount Number of remaining tasks.
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
     * @param task Added task.
     * @param taskCount Number of stored tasks.
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
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("OOPS! " + message);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }
}