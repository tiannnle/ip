/**
 * Represents the different commands understood by Kairo.
 */
public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    UNKNOWN;

    /**
     * Determines the command type represented by the input.
     *
     * @param input input entered by the user
     * @return matching command type, or UNKNOWN if unrecognised
     */
    public static CommandType fromInput(String input) {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            return UNKNOWN;
        }

        String commandWord = trimmedInput.split("\\s+", 2)[0];

        return switch (commandWord) {
            case "bye" -> trimmedInput.equals("bye") ? BYE : UNKNOWN;
            case "list" -> trimmedInput.equals("list") ? LIST : UNKNOWN;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "delete" -> DELETE;
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            default -> UNKNOWN;
        };
    }
}