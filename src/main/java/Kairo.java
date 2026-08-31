import java.util.Scanner;

/**
 * Represents the Kairo chatbot application.
 */
public class Kairo {

    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";

    /**
     * Starts the chatbot and allows the user to add, list, mark, and unmark
     * tasks.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
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
                    String statusIcon = isDone[i] ? "X" : " ";
                    System.out.println(
                            (i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }

                System.out.println(HORIZONTAL_LINE);
            } else if (input.startsWith("mark ")) {
                int taskIndex =
                        Integer.parseInt(input.substring("mark ".length())) - 1;
                isDone[taskIndex] = true;

                System.out.println(HORIZONTAL_LINE);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("[X] " + tasks[taskIndex]);
                System.out.println(HORIZONTAL_LINE);
            } else if (input.startsWith("unmark ")) {
                int taskIndex =
                        Integer.parseInt(input.substring("unmark ".length())) - 1;
                isDone[taskIndex] = false;

                System.out.println(HORIZONTAL_LINE);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("[ ] " + tasks[taskIndex]);
                System.out.println(HORIZONTAL_LINE);
            } else {
                tasks[taskCount] = input;
                isDone[taskCount] = false;
                taskCount++;

                System.out.println(HORIZONTAL_LINE);
                System.out.println("added: " + input);
                System.out.println(HORIZONTAL_LINE);
            }
        }

        System.out.println(HORIZONTAL_LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(HORIZONTAL_LINE);

        scanner.close();
    }
}