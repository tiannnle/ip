import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task occurring over a specified time period.
 */
public class Event extends Task {

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate from;
    private LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description description of the event
     * @param from starting time
     * @param to ending time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = LocalDate.parse(from);
        this.to = LocalDate.parse(to);
    }

    /**
     * Returns the event in its display format.
     *
     * @return formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns this event in the format used for file storage.
     *
     * @return event data formatted for storage
     */
    @Override
    public String toDataString() {
        return "E | " + super.toDataString()
                + " | " + from + " | " + to;
    }
}