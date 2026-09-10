package kairo;

import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Processes Kairo's commands and returns responses for the user interface.
 */
public class Kairo {

    private final Ui ui = new Ui();
    private final Storage storage;
    private TaskList tasks = new TaskList();
    private String startupError = "";
    private boolean isExit;

    /**
     * Creates Kairo using the default task file.
     */
    public Kairo() {
        this(Path.of("data", "kairo.txt"));
    }

    /**
     * Creates Kairo and loads tasks from the specified file.
     *
     * @param filePath Location of the task file.
     */
    public Kairo(Path filePath) {
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (KairoException exception) {
            startupError = exception.getMessage();
        }
    }

    /**
     * Runs the optional console interface.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Kairo kairo = new Kairo();
        System.out.println(kairo.getWelcomeMessage());
        try (Scanner scanner = new Scanner(System.in)) {
            while (!kairo.isExit() && scanner.hasNextLine()) {
                System.out.println(kairo.getResponse(scanner.nextLine()));
            }
        }
        if (!kairo.isExit()) {
            System.out.println(kairo.getResponse("bye"));
        }
    }

    /**
     * Returns the greeting, including any error encountered while loading tasks.
     *
     * @return Message to display when the application starts.
     */
    public String getWelcomeMessage() {
        String message = ui.getWelcomeMessage();
        if (!startupError.isEmpty()) {
            message += "\n\n" + ui.formatError(startupError);
        }
        return message;
    }

    /**
     * Processes one command and returns its response.
     *
     * @param input Complete command entered by the user.
     * @return Confirmation, task information, or an error message.
     */
    public String getResponse(String input) {
        if (isExit) {
            return ui.getGoodbyeMessage();
        }
        String command = input.trim();
        try {
            return processCommand(Parser.parseCommandType(command), command);
        } catch (KairoException exception) {
            return ui.formatError(exception.getMessage());
        }
    }

    /**
     * Checks whether the user has requested to exit.
     *
     * @return True after the bye command has been processed.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Executes a parsed command using this session's task list.
     *
     * @param commandType Type of command entered.
     * @param input Complete, trimmed command.
     * @return Response to display.
     * @throws KairoException If the command is invalid or saving fails.
     */
    private String processCommand(CommandType commandType, String input)
            throws KairoException {
        return switch (commandType) {
            case LIST -> ui.formatTaskList(tasks.getTasks());
            case MARK -> {
                int index = Parser.parseTaskIndex(input, "mark", tasks.size());
                Task task = tasks.mark(index);
                storage.save(tasks.getTasks());
                yield ui.formatMarked(task);
            }
            case UNMARK -> {
                int index = Parser.parseTaskIndex(input, "unmark", tasks.size());
                Task task = tasks.unmark(index);
                storage.save(tasks.getTasks());
                yield ui.formatUnmarked(task);
            }
            case DELETE -> {
                int index = Parser.parseTaskIndex(input, "delete", tasks.size());
                Task task = tasks.delete(index);
                storage.save(tasks.getTasks());
                yield ui.formatDeleted(task, tasks.size());
            }
            case TODO -> {
                String description = input.substring("todo".length()).trim();
                if (description.isEmpty()) {
                    throw new KairoException(
                            "The description of a todo cannot be empty.");
                }
                yield addTask(new Todo(description));
            }
            case DEADLINE -> addTask(parseDeadline(input));
            case EVENT -> addTask(parseEvent(input));
            case FIND -> {
                String keyword = input.substring("find".length()).trim();
                if (keyword.isEmpty()) {
                    throw new KairoException(
                            "Please provide a keyword to find.");
                }
                yield ui.formatMatchingTasks(tasks.find(keyword));
            }
            case BYE -> {
                isExit = true;
                yield ui.getGoodbyeMessage();
            }
            case UNKNOWN -> throw new KairoException(
                    "I'm sorry, but I don't know what that means.");
        };
    }

    /**
     * Adds a task and saves the updated list.
     *
     * @param task Task to add.
     * @return Confirmation containing the new task count.
     * @throws KairoException If saving fails.
     */
    private String addTask(Task task) throws KairoException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return ui.formatTaskAdded(task, tasks.size());
    }

    /**
     * Validates a deadline command and creates its task.
     *
     * @param input Complete deadline command.
     * @return Deadline described by the command.
     * @throws KairoException If the description, marker, or date is invalid.
     */
    private Deadline parseDeadline(String input) throws KairoException {
        String arguments = input.substring("deadline".length()).trim();
        int byPosition = arguments.indexOf(" /by ");
        if (byPosition <= 0
                || byPosition + " /by ".length() >= arguments.length()) {
            throw new KairoException(
                    "Use: deadline DESCRIPTION /by DATE");
        }
        String description = arguments.substring(0, byPosition).trim();
        String by = arguments.substring(byPosition + " /by ".length()).trim();
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
     * @param input Complete event command.
     * @return Event described by the command.
     * @throws KairoException If the description, markers, or dates are invalid.
     */
    private Event parseEvent(String input) throws KairoException {
        String arguments = input.substring("event".length()).trim();
        int fromPosition = arguments.indexOf(" /from ");
        int toPosition = arguments.indexOf(" /to ");
        if (fromPosition <= 0
                || toPosition <= fromPosition + " /from ".length()
                || toPosition + " /to ".length() >= arguments.length()) {
            throw new KairoException(
                    "Use: event DESCRIPTION /from START /to END");
        }
        String description = arguments.substring(0, fromPosition).trim();
        String from = arguments.substring(
                fromPosition + " /from ".length(), toPosition).trim();
        String to = arguments.substring(toPosition + " /to ".length()).trim();
        try {
            return new Event(description, from, to);
        } catch (DateTimeParseException exception) {
            throw new KairoException(
                    "Enter the event dates in yyyy-MM-dd format.");
        }
    }
}
