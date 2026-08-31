/**
 * Represents a task tracked by Kairo.
 */
public class Task {

    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol representing the completion status.
     *
     * @return {@code X} if completed, or a blank space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task in its display format.
     *
     * @return formatted task description and status
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}