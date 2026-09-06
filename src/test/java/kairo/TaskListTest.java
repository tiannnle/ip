package kairo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests task-management operations performed by TaskList.
 */
class TaskListTest {

    /**
     * Tests that marking and unmarking update a task's completion status.
     */
    @Test
    void markAndUnmark_existingTask_updatesCompletionStatus() {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList();
        taskList.add(task);

        assertFalse(task.isDone);

        Task markedTask = taskList.mark(0);

        assertSame(task, markedTask);
        assertTrue(task.isDone);

        Task unmarkedTask = taskList.unmark(0);

        assertSame(task, unmarkedTask);
        assertFalse(task.isDone);
    }

    /**
     * Tests that deleting removes and returns the selected task.
     */
    @Test
    void delete_existingTask_removesAndReturnsTask() {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList();
        taskList.add(task);

        Task removedTask = taskList.delete(0);

        assertSame(task, removedTask);
        assertEquals(0, taskList.size());
    }

    /**
     * Tests that modifying the returned list does not modify TaskList.
     */
    @Test
    void getTasks_returnedListDoesNotModifyOriginalList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        ArrayList<Task> returnedTasks = taskList.getTasks();
        returnedTasks.clear();

        assertEquals(1, taskList.size());
    }
}