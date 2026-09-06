import java.time.format.DateTimeParseException;

/**
 * Interprets and validates commands entered by the user.
 */
public final class Parser {

    private Parser() {
        // Prevents creation of Parser objects.
    }

    /**
     * Identifies the command type.
     *
     * @param input complete user input
     * @return corresponding command type
     */
    public static CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /**
     * Extracts and validates a task index.
     *
     * @param input complete user input
     * @param command command word
     * @param taskCount number of available tasks
     * @return zero-based task index
     * @throws KairoException if the task number is invalid
     */
    public static int parseTaskIndex(
            String input, String command, int taskCount)
            throws KairoException {
        String numberText =
                input.substring(command.length()).trim();

        if (!numberText.matches("\\d{1,3}")) {
            throw new KairoException(
                    "Please provide a valid task number to "
                            + command + ".");
        }

        int taskNumber = Integer.parseInt(numberText);

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new KairoException(
                    "Task number " + taskNumber + " does not exist.");
        }

        return taskNumber - 1;
    }

    /**
     * Creates a todo from user input.
     *
     * @param input complete todo command
     * @return parsed todo
     * @throws KairoException if the description is empty
     */
    public static Task parseTodo(String input) throws KairoException {
        String description =
                input.substring("todo".length()).trim();

        if (description.isEmpty()) {
            throw new KairoException(
                    "The description of a todo cannot be empty.");
        }

        return new Todo(description);
    }

    /**
     * Creates a deadline from user input.
     *
     * @param input complete deadline command
     * @return parsed deadline
     * @throws KairoException if the command is invalid
     */
    public static Task parseDeadline(String input)
            throws KairoException {
        String arguments =
                input.substring("deadline".length()).trim();
        int byPosition = arguments.indexOf(" /by ");

        if (byPosition <= 0
                || byPosition + " /by ".length()
                >= arguments.length()) {
            throw new KairoException(
                    "Use: deadline DESCRIPTION /by DATE");
        }

        String description =
                arguments.substring(0, byPosition).trim();
        String by = arguments.substring(
                byPosition + " /by ".length()).trim();

        try {
            return new Deadline(description, by);
        } catch (DateTimeParseException exception) {
            throw new KairoException(
                    "Enter the deadline date in yyyy-MM-dd format.");
        }
    }

    /**
     * Creates an event from user input.
     *
     * @param input complete event command
     * @return parsed event
     * @throws KairoException if the command is invalid
     */
    public static Task parseEvent(String input)
            throws KairoException {
        String arguments =
                input.substring("event".length()).trim();

        int fromPosition = arguments.indexOf(" /from ");
        int toPosition = arguments.indexOf(" /to ");

        if (fromPosition <= 0
                || toPosition
                <= fromPosition + " /from ".length()
                || toPosition + " /to ".length()
                >= arguments.length()) {
            throw new KairoException(
                    "Use: event DESCRIPTION "
                            + "/from START_DATE /to END_DATE");
        }

        String description =
                arguments.substring(0, fromPosition).trim();
        String from = arguments.substring(
                fromPosition + " /from ".length(),
                toPosition).trim();
        String to = arguments.substring(
                toPosition + " /to ".length()).trim();

        try {
            return new Event(description, from, to);
        } catch (DateTimeParseException exception) {
            throw new KairoException(
                    "Enter the event dates in yyyy-MM-dd format.");
        }
    }
}