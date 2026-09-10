package kairo;

import java.util.ArrayList;

/**
 * Formats responses shared by Kairo's graphical and console interfaces.
 */
public class Ui {

    /**
     * Creates a response formatter.
     */
    public Ui() {
    }

    /**
     * Returns Kairo's greeting.
     *
     * @return Welcome message.
     */
    public String getWelcomeMessage() {
        return "Hello! I'm Kairo.\nWhat can I do for you?";
    }

    /**
     * Returns Kairo's farewell.
     *
     * @return Goodbye message.
     */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Formats the full task list.
     *
     * @param tasks Tasks to display.
     * @return Numbered tasks or an empty-list message.
     */
    public String formatTaskList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your task list is empty.";
        }
        return "Here are the tasks in your list:\n" + formatNumberedTasks(tasks);
    }

    /**
     * Formats the results of a keyword search.
     *
     * @param matchingTasks Tasks matching the keyword.
     * @return Search results or a no-matches message.
     */
    public String formatMatchingTasks(ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "No matching tasks found.";
        }
        return "Here are the matching tasks in your list:\n"
                + formatNumberedTasks(matchingTasks);
    }

    /**
     * Formats confirmation that a task was completed.
     *
     * @param task Marked task.
     * @return Confirmation message.
     */
    public String formatMarked(Task task) {
        return "Nice! I've marked this task as done:\n" + task;
    }

    /**
     * Formats confirmation that a task was made incomplete.
     *
     * @param task Unmarked task.
     * @return Confirmation message.
     */
    public String formatUnmarked(Task task) {
        return "OK, I've marked this task as not done yet:\n" + task;
    }

    /**
     * Formats confirmation that a task was removed.
     *
     * @param task Deleted task.
     * @param taskCount Number of remaining tasks.
     * @return Confirmation message.
     */
    public String formatDeleted(Task task, int taskCount) {
        return "Noted. I've removed this task:\n" + task
                + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of stored tasks.
     * @return Confirmation message.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:\n" + task
                + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Formats an error for display.
     *
     * @param message Explanation of the error.
     * @return Error response.
     */
    public String formatError(String message) {
        return "OOPS! " + message;
    }

    /**
     * Gives each displayed task a one-based number.
     *
     * @param tasks Tasks to format.
     * @return Tasks separated by newlines.
     */
    private String formatNumberedTasks(ArrayList<Task> tasks) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                result.append("\n");
            }
            result.append(i + 1).append(".").append(tasks.get(i));
        }
        return result.toString();
    }

    /**
     * Formats the task count with the correct singular or plural noun.
     *
     * @param taskCount Number of stored tasks.
     * @return Task-count sentence.
     */
    private String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + taskWord + " in the list.";
    }
}
