package kairo;

/**
 * Parses commands and arguments entered by the user.
 */
public final class Parser {

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

        int taskNumber = Integer.parseInt(numberText);

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new KairoException(
                    "Task number " + taskNumber + " does not exist.");
        }

        return taskNumber - 1;
    }
}