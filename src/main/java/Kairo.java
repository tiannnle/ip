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
        Task[] tasks = new Task[100];
        int taskCount = 0;

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Hello! I'm Kairo.");
        System.out.println("What can I do for you?");
        System.out.println(HORIZONTAL_LINE);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            if (input.equals("bye")) {
                break;
            }

            try {
                taskCount = processCommand(input, tasks, taskCount);
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
     * @param input command entered by the user
     * @param tasks array containing the tasks
     * @param taskCount number of tasks currently stored
     * @return updated number of stored tasks
     * @throws KairoException if the command is invalid
     */
    private static int processCommand(
            String input, Task[] tasks, int taskCount)
            throws KairoException {

        if (input.equals("list")) {
            System.out.println(HORIZONTAL_LINE);

            for (int i = 0; i < taskCount; i++) {
                System.out.println((i + 1) + "." + tasks[i]);
            }

            System.out.println(HORIZONTAL_LINE);
            return taskCount;
        }

        if (input.equals("mark") || input.startsWith("mark ")) {
            int taskIndex =
                    parseTaskIndex(input, "mark", taskCount);
            tasks[taskIndex].markAsDone();

            System.out.println(HORIZONTAL_LINE);
            System.out.println("Nice! I've marked this task as done:");
            System.out.println(tasks[taskIndex]);
            System.out.println(HORIZONTAL_LINE);

            return taskCount;
        }

        if (input.equals("unmark") || input.startsWith("unmark ")) {
            int taskIndex =
                    parseTaskIndex(input, "unmark", taskCount);
            tasks[taskIndex].markAsNotDone();

            System.out.println(HORIZONTAL_LINE);
            System.out.println(
                    "OK, I've marked this task as not done yet:");
            System.out.println(tasks[taskIndex]);
            System.out.println(HORIZONTAL_LINE);

            return taskCount;
        }

        if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.equals("todo")
                    ? ""
                    : input.substring("todo ".length()).trim();

            if (description.isEmpty()) {
                throw new KairoException(
                        "The description of a todo cannot be empty.");
            }

            tasks[taskCount] = new Todo(description);
            taskCount++;

            printAddedTask(tasks[taskCount - 1], taskCount);
            return taskCount;
        }

        if (input.equals("deadline")
                || input.startsWith("deadline ")) {
            String arguments = input.equals("deadline")
                    ? ""
                    : input.substring("deadline ".length()).trim();

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

            tasks[taskCount] = new Deadline(description, by);
            taskCount++;

            printAddedTask(tasks[taskCount - 1], taskCount);
            return taskCount;
        }

        if (input.equals("event") || input.startsWith("event ")) {
            String arguments = input.equals("event")
                    ? ""
                    : input.substring("event ".length()).trim();

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

            tasks[taskCount] = new Event(description, from, to);
            taskCount++;

            printAddedTask(tasks[taskCount - 1], taskCount);
            return taskCount;
        }

        throw new KairoException(
                "I'm sorry, but I don't know what that means.");
    }

    /**
     * Extracts and validates a task index from a command.
     *
     * @param input full command entered by the user
     * @param command command word, such as mark or unmark
     * @param taskCount number of tasks currently stored
     * @return zero-based task index
     * @throws KairoException if the task number is invalid
     */
    private static int parseTaskIndex(
            String input, String command, int taskCount)
            throws KairoException {
        String numberText = input.equals(command)
                ? ""
                : input.substring((command + " ").length()).trim();

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