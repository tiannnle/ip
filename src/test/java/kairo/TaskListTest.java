package kairo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests task-management operations performed by TaskList.
 */
class TaskListTest {

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

    @Test
    void delete_existingTask_removesAndReturnsTask() {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList();
        taskList.add(task);

        Task removedTask = taskList.delete(0);

        assertSame(task, removedTask);
        assertEquals(0, taskList.size());
    }

    @Test
    void getTasks_returnedListDoesNotModifyOriginalList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        ArrayList<Task> returnedTasks = taskList.getTasks();
        returnedTasks.clear();

        assertEquals(1, taskList.size());
    }
}