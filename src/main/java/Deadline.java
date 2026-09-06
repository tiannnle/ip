import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description description of the task
     * @param by deadline information
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDate.parse(by);
    }

    /**
     * Returns the deadline in its display format.
     *
     * @return formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
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