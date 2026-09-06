import java.util.ArrayList;

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
     * @param tasks tasks loaded from storage
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a task at the specified index.
     *
     * @param index zero-based task index
     * @return task at the specified index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns a task.
     *
     * @param index zero-based task index
     * @return deleted task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks a task as completed.
     *
     * @param index zero-based task index
     * @return task that was marked
     */
    public Task mark(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as not completed.
     *
     * @param index zero-based task index
     * @return task that was unmarked
     */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns a copy of the stored tasks.
     *
     * @return copy of the task list
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }
}