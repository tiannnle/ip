package kairo;

import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses commands and arguments entered by the user.
 */
public final class Parser {

    private static final Pattern DATE_MARKER =
            Pattern.compile("(?<!\\S)/(?:by|from|to)(?!\\S)");

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
            case "sort" -> CommandType.SORT;
            default -> CommandType.UNKNOWN;
        };
    }

    /**
     * Validates a todo command and creates its task.
     *
     * @param input Complete, trimmed todo command.
     * @return Todo described by the command.
     * @throws KairoException If the description is empty or contains unsupported characters.
     */
    public static Todo parseTodo(String input) throws KairoException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new KairoException(
                    "The description of a todo cannot be empty.");
        }
        validateDescription(description);
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
        String[] fields = parseDateFields(
                arguments, "Use: deadline DESCRIPTION /by DATE", "/by");
        validateDescription(fields[0]);
        try {
            return new Deadline(fields[0], fields[1]);
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
        String[] fields = parseDateFields(
                arguments, "Use: event DESCRIPTION /from START /to END", "/from", "/to");
        validateDescription(fields[0]);
        try {
            return new Event(fields[0], fields[1], fields[2]);
        } catch (DateTimeParseException exception) {
            throw new KairoException(
                    "Enter the event dates in yyyy-MM-dd format.");
        } catch (IllegalArgumentException exception) {
            throw new KairoException(exception.getMessage());
        }
    }

    /**
     * Splits date arguments at standalone markers, allowing spaces or tabs.
     * Each expected marker must appear exactly once and in the given order.
     *
     * @param arguments Description followed by date markers and values.
     * @param usage Error message describing the required command format.
     * @param markers Required markers in their expected order.
     * @return Non-empty description and date fields.
     * @throws KairoException If markers or fields are missing or repeated.
     */
    private static String[] parseDateFields(
            String arguments, String usage, String... markers) throws KairoException {
        String[] fields = new String[markers.length + 1];
        Matcher matcher = DATE_MARKER.matcher(arguments);
        int fieldIndex = 0;
        int previousEnd = 0;
        while (matcher.find()) {
            if (fieldIndex >= markers.length || !matcher.group().equals(markers[fieldIndex])) {
                throw new KairoException(usage);
            }
            fields[fieldIndex] = arguments.substring(previousEnd, matcher.start()).trim();
            if (fields[fieldIndex].isEmpty()) {
                throw new KairoException(usage);
            }
            previousEnd = matcher.end();
            fieldIndex++;
        }
        if (fieldIndex != markers.length) {
            throw new KairoException(usage);
        }
        fields[fieldIndex] = arguments.substring(previousEnd).trim();
        if (fields[fieldIndex].isEmpty()) {
            throw new KairoException(usage);
        }
        return fields;
    }

    /**
     * Rejects characters that cannot safely be stored in a task description.
     *
     * @param description Task description to check.
     * @throws KairoException If the description contains a pipe or line break.
     */
    private static void validateDescription(String description) throws KairoException {
        if (description.contains("|")) {
            throw new KairoException("Please remove the pipe character (|) from the task description.");
        }
        if (description.contains("\n") || description.contains("\r")) {
            throw new KairoException("Please keep the task description on one line.");
        }
    }

    /**
     * Checks that a command such as list or bye has no extra arguments.
     *
     * @param input Complete command entered by the user.
     * @param command Command word expected on its own.
     * @throws KairoException If extra arguments were supplied.
     */
    public static void validateNoArguments(String input, String command) throws KairoException {
        if (!input.trim().equals(command)) {
            throw new KairoException("Use: " + command + " (without extra arguments)");
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
     * Extracts and validates the field used to sort tasks.
     *
     * @param input Complete sort command.
     * @return Either {@code name} or {@code date}.
     * @throws KairoException If the command does not specify one valid field.
     */
    public static String parseSortKey(String input) throws KairoException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2 || !parts[0].equals("sort")
                || (!parts[1].equals("name") && !parts[1].equals("date"))) {
            throw new KairoException("Use: sort name OR sort date");
        }
        return parts[1];
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
