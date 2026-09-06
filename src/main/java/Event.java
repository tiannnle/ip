/**
 * Represents a task occurring over a specified time period.
 */
public class Event extends Task {

    private String from;
    private String to;

    /**
     * Creates an event task.
     *
     * @param description description of the event
     * @param from starting time
     * @param to ending time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event in its display format.
     *
     * @return formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this event in the format used for file storage.
     *
     * @return event data formatted for storage
     */
    @Override
    public String toDataString() {
        return "E | " + super.toDataString() + " | " + from + " | " + to;
    }
}