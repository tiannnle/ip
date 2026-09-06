import java.nio.file.Path;

/**
 * Represents the Kairo chatbot application.
 */
public class Kairo {

    private static final Ui UI = new Ui();

    private static final Storage STORAGE =
            new Storage(Path.of("data", "kairo.txt"));

    /**
     * Starts the chatbot and processes task-related commands.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        TaskList tasks;

        UI.showWelcome();

        try {
            tasks = new TaskList(STORAGE.load());
        } catch (KairoException exception) {
            UI.showError(exception.getMessage());
            tasks = new TaskList();
        }

        while (UI.hasNextCommand()) {
            String input = UI.readCommand();
            CommandType commandType =
                    Parser.parseCommandType(input);

            if (commandType == CommandType.BYE) {
                break;
            }

            try {
                processCommand(commandType, input, tasks);
            } catch (KairoException exception) {
                UI.showError(exception.getMessage());
            }
        }

        UI.showGoodbye();
        UI.close();
    }

    /**
     * Processes one command entered by the user.
     *
     * @param commandType type of command entered
     * @param input complete user input
     * @param tasks task list
     * @throws KairoException if the command is invalid
     */
    private static void processCommand(
            CommandType commandType, String input,
            TaskList tasks) throws KairoException {

        switch (commandType) {
            case LIST -> UI.showTaskList(tasks.getTasks());

            case MARK -> {
                int taskIndex = Parser.parseTaskIndex(
                        input, "mark", tasks.size());
                Task task = tasks.mark(taskIndex);

                STORAGE.save(tasks.getTasks());
                UI.showMarked(task);
            }

            case UNMARK -> {
                int taskIndex = Parser.parseTaskIndex(
                        input, "unmark", tasks.size());
                Task task = tasks.unmark(taskIndex);

                STORAGE.save(tasks.getTasks());
                UI.showUnmarked(task);
            }

            case DELETE -> {
                int taskIndex = Parser.parseTaskIndex(
                        input, "delete", tasks.size());
                Task removedTask = tasks.delete(taskIndex);

                STORAGE.save(tasks.getTasks());
                UI.showDeleted(removedTask, tasks.size());
            }

            case TODO -> {
                Task task = Parser.parseTodo(input);

                tasks.add(task);
                STORAGE.save(tasks.getTasks());
                UI.showTaskAdded(task, tasks.size());
            }

            case DEADLINE -> {
                Task task = Parser.parseDeadline(input);

                tasks.add(task);
                STORAGE.save(tasks.getTasks());
                UI.showTaskAdded(task, tasks.size());
            }

            case EVENT -> {
                Task task = Parser.parseEvent(input);

                tasks.add(task);
                STORAGE.save(tasks.getTasks());
                UI.showTaskAdded(task, tasks.size());
            }

            case UNKNOWN -> throw new KairoException(
                    "I'm sorry, but I don't know what that means.");

            case BYE -> {
                // BYE is handled in main before this method is called.
            }

            default -> throw new KairoException(
                    "Unexpected command type.");
        }
    }
}