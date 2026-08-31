/**
 * Represents a task tracked by Kairo.
 */
public class Task {

    protected String description;
    protected boolean isDone;
    protected String taskType;
    protected String timeDetails;

    /**
     * Creates a task with its type and optional time information.
     *
     * @param description description of the task
     * @param taskType letter representing the task type
     * @param timeDetails formatted time information
     */
    public Task(String description, String taskType, String timeDetails) {
        this.description = description;
        this.taskType = taskType;
        this.timeDetails = timeDetails;
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
     * @return formatted task type, status, description, and time details
     */
    @Override
    public String toString() {
        return "[" + taskType + "][" + getStatusIcon() + "] "
                + description + timeDetails;
    }
}