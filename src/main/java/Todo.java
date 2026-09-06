/**
 * Represents a task without any date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo task.
     *
     * @param description description of the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo in its display format.
     *
     * @return formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns this todo in the format used for file storage.
     *
     * @return todo data formatted for storage
     */
    @Override
    public String toDataString() {
        return "T | " + super.toDataString();
    }
}
