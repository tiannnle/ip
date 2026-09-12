package kairo;

import java.nio.file.Path;
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
            case MARK -> markTask(input);
            case UNMARK -> unmarkTask(input);
            case DELETE -> deleteTask(input);
            case TODO -> addTask(Parser.parseTodo(input));
            case DEADLINE -> addTask(Parser.parseDeadline(input));
            case EVENT -> addTask(Parser.parseEvent(input));
            case FIND -> findTasks(input);
            case SORT -> sortTasks(input);
            case BYE -> exit();
            default -> throw new KairoException(
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
     * Marks a task as completed and saves the updated list.
     *
     * @param input Complete mark command.
     * @return Confirmation containing the marked task.
     * @throws KairoException If the task number is invalid or saving fails.
     */
    private String markTask(String input) throws KairoException {
        int index = Parser.parseTaskIndex(input, "mark", tasks.size());
        Task task = tasks.mark(index);
        storage.save(tasks.getTasks());
        return ui.formatMarked(task);
    }

    /**
     * Marks a task as incomplete and saves the updated list.
     *
     * @param input Complete unmark command.
     * @return Confirmation containing the unmarked task.
     * @throws KairoException If the task number is invalid or saving fails.
     */
    private String unmarkTask(String input) throws KairoException {
        int index = Parser.parseTaskIndex(input, "unmark", tasks.size());
        Task task = tasks.unmark(index);
        storage.save(tasks.getTasks());
        return ui.formatUnmarked(task);
    }

    /**
     * Deletes a task and saves the updated list.
     *
     * @param input Complete delete command.
     * @return Confirmation containing the removed task and remaining count.
     * @throws KairoException If the task number is invalid or saving fails.
     */
    private String deleteTask(String input) throws KairoException {
        int index = Parser.parseTaskIndex(input, "delete", tasks.size());
        Task task = tasks.delete(index);
        storage.save(tasks.getTasks());
        return ui.formatDeleted(task, tasks.size());
    }

    /**
     * Finds tasks whose descriptions contain the requested keyword.
     *
     * @param input Complete find command.
     * @return Matching tasks or a no-matches message.
     * @throws KairoException If the keyword is missing.
     */
    private String findTasks(String input) throws KairoException {
        String keyword = Parser.parseFindKeyword(input);
        return ui.formatMatchingTasks(tasks.find(keyword));
    }

    /**
     * Sorts a copy of the task list and saves it before replacing the active list.
     *
     * @param input Complete sort command.
     * @return Task list with its new order and numbering.
     * @throws KairoException If the sort field is invalid or saving fails.
     */
    private String sortTasks(String input) throws KairoException {
        String sortKey = Parser.parseSortKey(input);
        TaskList sortedTasks = new TaskList(tasks.getTasks());
        if (sortKey.equals("name")) {
            sortedTasks.sortByName();
        } else {
            sortedTasks.sortByDate();
        }
        storage.save(sortedTasks.getTasks());
        tasks = sortedTasks;
        return ui.formatTaskList(tasks.getTasks());
    }

    /**
     * Ends this session and creates its farewell response.
     *
     * @return Goodbye message.
     */
    private String exit() {
        isExit = true;
        return ui.getGoodbyeMessage();
    }
}
