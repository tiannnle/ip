package kairo;

import java.time.format.DateTimeParseException;

/**
 * Parses commands and arguments entered by the user.
 */
public final class Parser {

    private static final String DEADLINE_MARKER = " /by ";
    private static final String EVENT_START_MARKER = " /from ";
    private static final String EVENT_END_MARKER = " /to ";

    private Parser() {
    }

    /**
     * Determines the command type represented by the input.
     *
     * @param input Complete user input.
     * @return Corresponding command type.
     */
    public static CommandType parseCommandType(String input) {
        String commandWord = input.trim().split("\\s+", 2)[0];

        return switch (commandWord) {
            case "bye" -> CommandType.BYE;
            case "list" -> CommandType.LIST;
            case "mark" -> CommandType.MARK;
            case "unmark" -> CommandType.UNMARK;
            case "delete" -> CommandType.DELETE;
            case "todo" -> CommandType.TODO;
            case "deadline" -> CommandType.DEADLINE;
            case "event" -> CommandType.EVENT;
            case "find" -> CommandType.FIND;
            default -> CommandType.UNKNOWN;
        };
    }

    /**
     * Validates a todo command and creates its task.
     *
     * @param input Complete, trimmed todo command.
     * @return Todo described by the command.
     * @throws KairoException If the description is empty.
     */
    public static Todo parseTodo(String input) throws KairoException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new KairoException(
                    "The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Validates a deadline command and creates its task.
     *
     * @param input Complete, trimmed deadline command.
     * @return Deadline described by the command.
     * @throws KairoException If the description, marker, or date is invalid.
     */
    public static Deadline parseDeadline(String input) throws KairoException {
        String arguments = input.substring("deadline".length()).trim();
        int byPosition = arguments.indexOf(DEADLINE_MARKER);
        if (byPosition <= 0
                || byPosition + DEADLINE_MARKER.length() >= arguments.length()) {
            throw new KairoException(
                    "Use: deadline DESCRIPTION /by DATE");
        }
        String description = arguments.substring(0, byPosition).trim();
        String by = arguments.substring(byPosition + DEADLINE_MARKER.length()).trim();
        try {
            return new Deadline(description, by);
        } catch (DateTimeParseException exception) {
            throw new KairoException(
                    "Enter the deadline date in yyyy-MM-dd format.");
        }
    }

    /**
     * Validates an event command and creates its task.
     *
     * @param input Complete, trimmed event command.
     * @return Event described by the command.
     * @throws KairoException If the description, markers, or dates are invalid.
     */
    public static Event parseEvent(String input) throws KairoException {
        String arguments = input.substring("event".length()).trim();
        int fromPosition = arguments.indexOf(EVENT_START_MARKER);
        int toPosition = arguments.indexOf(EVENT_END_MARKER);
        if (fromPosition <= 0
                || toPosition <= fromPosition + EVENT_START_MARKER.length()
                || toPosition + EVENT_END_MARKER.length() >= arguments.length()) {
            throw new KairoException(
                    "Use: event DESCRIPTION /from START /to END");
        }
        String description = arguments.substring(0, fromPosition).trim();
        String from = arguments.substring(
                fromPosition + EVENT_START_MARKER.length(), toPosition).trim();
        String to = arguments.substring(toPosition + EVENT_END_MARKER.length()).trim();
        try {
            return new Event(description, from, to);
        } catch (DateTimeParseException exception) {
            throw new KairoException(
                    "Enter the event dates in yyyy-MM-dd format.");
        }
    }

    /**
     * Extracts the non-empty keyword from a find command.
     *
     * @param input Complete, trimmed find command.
     * @return Keyword to match against task descriptions.
     * @throws KairoException If the keyword is empty.
     */
    public static String parseFindKeyword(String input) throws KairoException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new KairoException(
                    "Please provide a keyword to find.");
        }
        return keyword;
    }

    /**
     * Extracts and validates a task index from a command.
     *
     * @param input Complete user input.
     * @param command Command word, such as mark or delete.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based task index.
     * @throws KairoException If the task number is invalid.
     */
    public static int parseTaskIndex(
            String input, String command, int taskCount)
            throws KairoException {
        String numberText = input.substring(command.length()).trim();

        if (!numberText.matches("\\d+")) {
            throw new KairoException(
                    "Please provide a valid task number to "
                            + command + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new KairoException(
                    "Please provide a valid task number to " + command + ".");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new KairoException(
                    "Task number " + taskNumber + " does not exist.");
        }

        return taskNumber - 1;
    }
}
