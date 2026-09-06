/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {

    private String by;

    /**
     * Creates a deadline task.
     *
     * @param description description of the task
     * @param by deadline information
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline in its display format.
     *
     * @return formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }

    /**
     * Returns this deadline in the format used for file storage.
     *
     * @return deadline data formatted for storage
     */
    @Override
    public String toDataString() {
        return "D | " + super.toDataString() + " | " + by;
    }
}