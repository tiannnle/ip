package kairo;

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
        int previousSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousSize + 1
                : "Adding a task must increase the task count by one";
        assert tasks.get(tasks.size() - 1) == task
                : "The new task must be appended to the list";
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
        assert task.isDone : "A marked task must be completed";
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
        assert !task.isDone : "An unmarked task must be incomplete";
        return task;
    }

    /**
     * Deletes a task.
     *
     * @param index Zero-based task index.
     * @return Deleted task.
     */
    public Task delete(int index) {
        int previousSize = tasks.size();
        Task removedTask = tasks.remove(index);
        assert tasks.size() == previousSize - 1
                : "Deleting a task must decrease the task count by one";
        return removedTask;
    }

    /**
     * Finds tasks containing the specified keyword in their descriptions.
     *
     * @param keyword Keyword to search for.
     * @return Tasks with matching descriptions.
     */
    public ArrayList<Task> find(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        String normalizedKeyword = keyword.toLowerCase();

        for (Task task : tasks) {
            if (task.description.toLowerCase().contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
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
