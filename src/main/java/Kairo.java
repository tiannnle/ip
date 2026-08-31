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

            if (input.equals("list")) {
                System.out.println(HORIZONTAL_LINE);

                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }

                System.out.println(HORIZONTAL_LINE);
            } else if (input.equals("mark")
                    || input.startsWith("mark ")) {
                String numberText = input.equals("mark")
                        ? ""
                        : input.substring("mark ".length()).trim();

                if (!isValidTaskNumber(numberText, taskCount)) {
                    printError("Please provide a valid task number to mark.");
                } else {
                    int taskIndex = Integer.parseInt(numberText) - 1;
                    tasks[taskIndex].markAsDone();

                    System.out.println(HORIZONTAL_LINE);
                    System.out.println(
                            "Nice! I've marked this task as done:");
                    System.out.println(tasks[taskIndex]);
                    System.out.println(HORIZONTAL_LINE);
                }
            } else if (input.equals("unmark")
                    || input.startsWith("unmark ")) {
                String numberText = input.equals("unmark")
                        ? ""
                        : input.substring("unmark ".length()).trim();

                if (!isValidTaskNumber(numberText, taskCount)) {
                    printError("Please provide a valid task number to unmark.");
                } else {
                    int taskIndex = Integer.parseInt(numberText) - 1;
                    tasks[taskIndex].markAsNotDone();

                    System.out.println(HORIZONTAL_LINE);
                    System.out.println(
                            "OK, I've marked this task as not done yet:");
                    System.out.println(tasks[taskIndex]);
                    System.out.println(HORIZONTAL_LINE);
                }
            } else if (input.equals("todo")
                    || input.startsWith("todo ")) {
                String description = input.equals("todo")
                        ? ""
                        : input.substring("todo ".length()).trim();

                if (description.isEmpty()) {
                    printError("The description of a todo cannot be empty.");
                } else {
                    tasks[taskCount] = new Todo(description);
                    taskCount++;

                    printAddedTask(tasks[taskCount - 1], taskCount);
                }
            } else if (input.equals("deadline")
                    || input.startsWith("deadline ")) {
                String arguments = input.equals("deadline")
                        ? ""
                        : input.substring("deadline ".length()).trim();

                int byPosition = arguments.indexOf(" /by ");

                if (byPosition <= 0
                        || byPosition + " /by ".length()
                        >= arguments.length()) {
                    printError(
                            "Use: deadline DESCRIPTION /by TIME");
                } else {
                    String description =
                            arguments.substring(0, byPosition).trim();
                    String by = arguments.substring(
                            byPosition + " /by ".length()).trim();

                    tasks[taskCount] = new Deadline(description, by);
                    taskCount++;

                    printAddedTask(tasks[taskCount - 1], taskCount);
                }
            } else if (input.equals("event")
                    || input.startsWith("event ")) {
                String arguments = input.equals("event")
                        ? ""
                        : input.substring("event ".length()).trim();

                int fromPosition = arguments.indexOf(" /from ");
                int toPosition = arguments.indexOf(" /to ");

                if (fromPosition <= 0
                        || toPosition <= fromPosition + " /from ".length()
                        || toPosition + " /to ".length()
                        >= arguments.length()) {
                    printError(
                            "Use: event DESCRIPTION /from START /to END");
                } else {
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
                }
            } else {
                printError(
                        "I'm sorry, but I don't know what that means.");
            }
        }

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(HORIZONTAL_LINE);

        scanner.close();
    }

    /**
     * Checks whether the input represents an existing task number.
     *
     * @param numberText task number entered by the user
     * @param taskCount number of tasks currently stored
     * @return true if the task number is valid
     */
    private static boolean isValidTaskNumber(
            String numberText, int taskCount) {
        if (!numberText.matches("\\d{1,3}")) {
            return false;
        }

        int taskNumber = Integer.parseInt(numberText);
        return taskNumber >= 1 && taskNumber <= taskCount;
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
                "Now you have " + taskCount + " " + taskWord + " in the list.");
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