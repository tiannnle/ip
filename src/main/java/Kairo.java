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
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                break;
            }

            if (input.equals("list")) {
                System.out.println(HORIZONTAL_LINE);

                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }

                System.out.println(HORIZONTAL_LINE);
            } else if (input.startsWith("mark ")) {
                int taskIndex =
                        Integer.parseInt(input.substring("mark ".length())) - 1;
                tasks[taskIndex].markAsDone();

                System.out.println(HORIZONTAL_LINE);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(tasks[taskIndex]);
                System.out.println(HORIZONTAL_LINE);
            } else if (input.startsWith("unmark ")) {
                int taskIndex =
                        Integer.parseInt(input.substring("unmark ".length())) - 1;
                tasks[taskIndex].markAsNotDone();

                System.out.println(HORIZONTAL_LINE);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println(tasks[taskIndex]);
                System.out.println(HORIZONTAL_LINE);
            } else if (input.startsWith("todo ")) {
                String description = input.substring("todo ".length());

                tasks[taskCount] = new Todo(description);
                taskCount++;

                printAddedTask(tasks[taskCount - 1], taskCount);
            } else if (input.startsWith("deadline ")) {
                String arguments = input.substring("deadline ".length());
                String[] parts = arguments.split(" /by ", 2);

                String description = parts[0];
                String by = parts[1];

                tasks[taskCount] = new Deadline(description, by);
                taskCount++;

                printAddedTask(tasks[taskCount - 1], taskCount);
            } else if (input.startsWith("event ")) {
                String arguments = input.substring("event ".length());
                String[] fromParts = arguments.split(" /from ", 2);
                String[] toParts = fromParts[1].split(" /to ", 2);

                String description = fromParts[0];
                String from = toParts[0];
                String to = toParts[1];

                tasks[taskCount] = new Event(description, from, to);
                taskCount++;

                printAddedTask(tasks[taskCount - 1], taskCount);
            }
        }

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(HORIZONTAL_LINE);

        scanner.close();
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
}
