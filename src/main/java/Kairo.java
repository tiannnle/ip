import java.nio.file.Path;
import java.time.format.DateTimeParseException;

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
            CommandType commandType = CommandType.fromInput(input);

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
                int taskIndex =
                        parseTaskIndex(input, "mark", tasks.size());
                Task task = tasks.mark(taskIndex);
                STORAGE.save(tasks.getTasks());

                UI.showMarked(task);
            }

            case UNMARK -> {
                int taskIndex =
                        parseTaskIndex(input, "unmark", tasks.size());
                Task task = tasks.unmark(taskIndex);
                STORAGE.save(tasks.getTasks());

                UI.showUnmarked(task);
            }

            case DELETE -> {
                int taskIndex =
                        parseTaskIndex(input, "delete", tasks.size());
                Task removedTask = tasks.delete(taskIndex);
                STORAGE.save(tasks.getTasks());

                UI.showDeleted(removedTask, tasks.size());
            }

            case TODO -> {
                String description =
                        input.substring("todo".length()).trim();

                if (description.isEmpty()) {
                    throw new KairoException(
                            "The description of a todo cannot be empty.");
                }

                Task task = new Todo(description);
                tasks.add(task);
                STORAGE.save(tasks.getTasks());
                UI.showTaskAdded(task, tasks.size());
            }

            case DEADLINE -> {
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

                Task task;

                try {
                    task = new Deadline(description, by);
                } catch (DateTimeParseException exception) {
                    throw new KairoException(
                            "Enter the deadline date in yyyy-MM-dd format.");
                }

                tasks.add(task);
                STORAGE.save(tasks.getTasks());
                UI.showTaskAdded(task, tasks.size());
            }

            case EVENT -> {
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

                Task task;

                try {
                    task = new Event(description, from, to);
                } catch (DateTimeParseException exception) {
                    throw new KairoException(
                            "Enter the event dates in yyyy-MM-dd format.");
                }

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

    /**
     * Extracts and validates a task index from a command.
     *
     * @param input full command entered by the user
     * @param command command word, such as mark or delete
     * @param taskCount number of tasks currently stored
     * @return zero-based task index
     * @throws KairoException if the task number is invalid
     */
    private static int parseTaskIndex(
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
}