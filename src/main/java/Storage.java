import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Handles saving Kairo's tasks to a file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that uses the specified file.
     *
     * @param filePath location of the data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks tasks to save
     * @throws KairoException if the file cannot be written
     */
    public void save(ArrayList<Task> tasks) throws KairoException {
        try {
            Path parentDirectory = filePath.getParent();

            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            ArrayList<String> lines = new ArrayList<>();

            for (Task task : tasks) {
                lines.add(task.toDataString());
            }

            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new KairoException("I could not save your tasks.");
        }
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @return list of tasks loaded from the file
     * @throws KairoException if the file cannot be read or contains invalid data
     */
    public ArrayList<Task> load() throws KairoException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            for (String line
                    : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(parseTask(line));
                }
            }
        } catch (IOException e) {
            throw new KairoException("I could not load your saved tasks.");
        }

        return tasks;
    }

    /**
     * Converts one saved line into a task.
     *
     * @param line saved task data
     * @return task represented by the saved data
     * @throws KairoException if the saved data is invalid
     */
    private Task parseTask(String line) throws KairoException {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3) {
            throw new KairoException("The saved task data is invalid.");
        }

        boolean isDone;

        if (parts[1].equals("1")) {
            isDone = true;
        } else if (parts[1].equals("0")) {
            isDone = false;
        } else {
            throw new KairoException("The saved task data is invalid.");
        }

        Task task = switch (parts[0]) {
            case "T" -> {
                if (parts.length != 3) {
                    throw new KairoException(
                            "The saved todo data is invalid.");
                }
                yield new Todo(parts[2]);
            }

            case "D" -> {
                if (parts.length != 4) {
                    throw new KairoException(
                            "The saved deadline data is invalid.");
                }
                yield new Deadline(parts[2], parts[3]);
            }

            case "E" -> {
                if (parts.length != 5) {
                    throw new KairoException(
                            "The saved event data is invalid.");
                }
                yield new Event(parts[2], parts[3], parts[4]);
            }

            default -> throw new KairoException(
                    "The saved task type is invalid.");
        };

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }
}