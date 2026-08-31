import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents the Kairo chatbot application.
 */
public class Kairo {

    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";

    /**
     * Starts the chatbot and processes task-related commands.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Hello! I'm Kairo.");
        System.out.println("What can I do for you?");
        System.out.println(HORIZONTAL_LINE);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            CommandType commandType = CommandType.fromInput(input);

            if (commandType == CommandType.BYE) {
                break;
            }

            try {
                processCommand(commandType, input, tasks);
            } catch (KairoException exception) {
                printError(exception.getMessage());
            }
        }

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(HORIZONTAL_LINE);

        scanner.close();
    }

    /**
     * Processes one command entered by the user.
     *
     * @param commandType type of command entered
     * @param input complete user input
     * @param tasks list containing the tasks
     * @throws KairoException if the command is invalid
     */
    private static void processCommand(
            CommandType commandType, String input,
            ArrayList<Task> tasks) throws KairoException {

        switch (commandType) {
            case LIST -> {
                System.out.println(HORIZONTAL_LINE);

                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i));
                }

                System.out.println(HORIZONTAL_LINE);
            }

            case MARK -> {
                int taskIndex =
                        parseTaskIndex(input, "mark", tasks.size());
                tasks.get(taskIndex).markAsDone();

                System.out.println(HORIZONTAL_LINE);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(tasks.get(taskIndex));
                System.out.println(HORIZONTAL_LINE);
            }

            case UNMARK -> {
                int taskIndex =
                        parseTaskIndex(input, "unmark", tasks.size());
                tasks.get(taskIndex).markAsNotDone();

                System.out.println(HORIZONTAL_LINE);
                System.out.println(
                        "OK, I've marked this task as not done yet:");
                System.out.println(tasks.get(taskIndex));
                System.out.println(HORIZONTAL_LINE);
            }

            case DELETE -> {
                int taskIndex =
                        parseTaskIndex(input, "delete", tasks.size());
                Task removedTask = tasks.remove(taskIndex);

                String taskWord = tasks.size() == 1 ? "task" : "tasks";

                System.out.println(HORIZONTAL_LINE);
                System.out.println("Noted. I've removed this task:");
                System.out.println(removedTask);
                System.out.println(
                        "Now you have " + tasks.size() + " "
                                + taskWord + " in the list.");
                System.out.println(HORIZONTAL_LINE);
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
                printAddedTask(task, tasks.size());
            }

            case DEADLINE -> {
                String arguments =
                        input.substring("deadline".length()).trim();
                int byPosition = arguments.indexOf(" /by ");

                if (byPosition <= 0
                        || byPosition + " /by ".length()
                        >= arguments.length()) {
                    throw new KairoException(
                            "Use: deadline DESCRIPTION /by TIME");
                }

                String description =
                        arguments.substring(0, byPosition).trim();
                String by = arguments.substring(
                        byPosition + " /by ".length()).trim();

                Task task = new Deadline(description, by);
                tasks.add(task);
                printAddedTask(task, tasks.size());
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

                Task task = new Event(description, from, to);
                tasks.add(task);
                printAddedTask(task, tasks.size());
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

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount number of tasks currently stored
     */
    private static void printAddedTask(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println(
                "Now you have " + taskCount + " " + taskWord
                        + " in the list.");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays an error message.
     *
     * @param message explanation of the error
     */
    private static void printError(String message) {
        System.out.println(HORIZONTAL_LINE);
        System.out.println("OOPS! " + message);
        System.out.println(HORIZONTAL_LINE);
    }
}