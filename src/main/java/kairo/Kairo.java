package kairo;

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
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        TaskList tasks = new TaskList();

        UI.showWelcome();

        try {
            tasks = new TaskList(STORAGE.load());
        } catch (KairoException exception) {
            UI.showError(exception.getMessage());
        }

        while (UI.hasNextCommand()) {
            String input = UI.readCommand();
            CommandType commandType = Parser.parseCommandType(input);

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
     * @param commandType Type of command entered.
     * @param input Complete user input.
     * @param tasks Task list managed by Kairo.
     * @throws KairoException If the command is invalid.
     */
    private static void processCommand(
            CommandType commandType, String input,
            TaskList tasks) throws KairoException {

        switch (commandType) {
            case LIST -> UI.showTaskList(tasks.getTasks());

            case MARK -> {
                int taskIndex =
                        Parser.parseTaskIndex(input, "mark", tasks.size());
                Task task = tasks.mark(taskIndex);
                STORAGE.save(tasks.getTasks());
                UI.showMarked(task);
            }

            case UNMARK -> {
                int taskIndex =
                        Parser.parseTaskIndex(input, "unmark", tasks.size());
                Task task = tasks.unmark(taskIndex);
                STORAGE.save(tasks.getTasks());
                UI.showUnmarked(task);
            }

            case DELETE -> {
                int taskIndex =
                        Parser.parseTaskIndex(input, "delete", tasks.size());
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
                            "Use: event DESCRIPTION /from START /to END");
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

            case FIND -> {
                String keyword =
                        input.substring("find".length()).trim();

                if (keyword.isEmpty()) {
                    throw new KairoException(
                            "Please provide a keyword to find.");
                }

                UI.showMatchingTasks(tasks.find(keyword));
            }

            case UNKNOWN -> throw new KairoException(
                    "I'm sorry, but I don't know what that means.");

            case BYE -> {
                // BYE is handled in main.
            }

            default -> throw new KairoException(
                    "Unexpected command type.");
        }
    }
}