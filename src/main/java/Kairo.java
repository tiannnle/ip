import java.util.Scanner;

/**
 * Represents the Kairo chatbot application.
 */
public class Kairo {

    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";

    /**
     * Starts the chatbot and allows the user to add and list tasks.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
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
                    System.out.println((i + 1) + ". " + tasks[i]);
                }

                System.out.println(HORIZONTAL_LINE);
            } else {
                tasks[taskCount] = input;
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