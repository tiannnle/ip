package kairo;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Stores and manages Kairo's task list.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing previously loaded tasks.
     *
     * @param tasks Tasks loaded from storage.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Marks a task as completed.
     *
     * @param index Zero-based task index.
     * @return Task that was marked.
     */
    public Task mark(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as incomplete.
     *
     * @param index Zero-based task index.
     * @return Task that was unmarked.
     */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Deletes a task.
     *
     * @param index Zero-based task index.
     * @return Deleted task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Finds tasks containing the specified keyword in their descriptions.
     *
     * @param keyword Keyword to search for.
     * @return Tasks with matching descriptions.
     */
    public ArrayList<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();

        return tasks.stream()
                .filter(task -> task.description.toLowerCase().contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a copy of the stored tasks.
     *
     * @return Copy of the task list.
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }
}