package kairo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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
     * Writes tasks to a temporary file before atomically replacing the data file.
     *
     * @param tasks tasks to save
     * @throws KairoException if the file cannot be written
     */
    public void save(ArrayList<Task> tasks) throws KairoException {
        Path temporaryFile = null;
        try {
            Path targetFile = filePath.toAbsolutePath();
            if (Files.isDirectory(targetFile)) {
                throw new IOException("The task file location is a directory.");
            }
            Path parentDirectory = targetFile.getParent();
            Files.createDirectories(parentDirectory);
            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toDataString());
            }
            temporaryFile = Files.createTempFile(parentDirectory, "kairo-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, targetFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new KairoException("I could not save your tasks.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException e) {
                    // Cleanup must not hide the result of saving the task file.
                }
            }
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
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    try {
                        tasks.add(parseTask(line));
                    } catch (KairoException | DateTimeParseException | IllegalArgumentException e) {
                        throw new KairoException("Invalid saved task at line " + (i + 1)
                                + ". Fix the task file and restart Kairo.");
                    }
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
        if (parts[2].isBlank()) {
            throw new KairoException("A saved task description cannot be empty.");
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
