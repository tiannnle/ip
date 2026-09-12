package kairo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
        String normalizedKeyword = keyword.toLowerCase();

        return tasks.stream()
                .filter(task -> task.description.toLowerCase().contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Sorts tasks alphabetically by description, ignoring case.
     * Tasks with equal descriptions retain their relative order.
     */
    public void sortByName() {
        tasks.sort(Comparator.comparing(
                task -> task.description, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Sorts tasks by deadline date or event start date, earliest first.
     * Tasks without dates appear last. Equal dates retain their relative order.
     */
    public void sortByDate() {
        tasks.sort(Comparator.comparing(
                TaskList::getSortDate, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Finds the date used to place a task in chronological order.
     *
     * @param task Task to inspect.
     * @return Deadline or event start date, or null for a task without a date.
     */
    private static LocalDate getSortDate(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getDeadlineDate();
        }
        if (task instanceof Event event) {
            return event.getStartDate();
        }
        return null;
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
