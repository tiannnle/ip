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
}