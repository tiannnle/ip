package kairo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the command-response interface used by the GUI and its persistence.
 */
class KairoTest {

    @TempDir
    Path temporaryDirectory;

    /**
     * Checks that all task types survive a new application session.
     *
     * @throws IOException If the temporary file cannot be read.
     */
    @Test
    void getResponse_addAllTaskTypes_savesAndReloadsTasks() throws IOException {
        Path file = temporaryDirectory.resolve("data/kairo.txt");
        Kairo kairo = new Kairo(file);
        assertTrue(kairo.getResponse("  todo read book  ").contains("[T][ ] read book"));
        assertTrue(kairo.getResponse("deadline report /by 2026-09-15").contains("[D][ ] report"));
        assertTrue(kairo.getResponse(
                "event meeting /from 2026-09-16 /to 2026-09-17").contains("[E][ ] meeting"));

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 0 | report | 2026-09-15",
                "E | 0 | meeting | 2026-09-16 | 2026-09-17"), Files.readAllLines(file));
        assertEquals(kairo.getResponse("list"), new Kairo(file).getResponse("list"));
    }

    /**
     * Checks that marking, unmarking, and deleting save their results.
     */
    @Test
    void getResponse_changeTasks_persistsEachChange() {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Kairo kairo = new Kairo(file);
        kairo.getResponse("todo read book");
        kairo.getResponse("mark 1");
        assertTrue(new Kairo(file).getResponse("list").contains("[T][X] read book"));
        kairo.getResponse("unmark 1");
        assertTrue(new Kairo(file).getResponse("list").contains("[T][ ] read book"));
        kairo.getResponse("delete 1");
        assertEquals("Your task list is empty.", new Kairo(file).getResponse("list"));
    }

    /**
     * Checks that search remains case-insensitive and does not change the list.
     */
    @Test
    void getResponse_findKeyword_returnsMatchesWithoutChangingTasks() {
        Kairo kairo = new Kairo(temporaryDirectory.resolve("kairo.txt"));
        kairo.getResponse("todo Read book");
        kairo.getResponse("todo buy milk");
        String before = kairo.getResponse("list");
        String result = kairo.getResponse("find BOOK");
        assertTrue(result.contains("Read book"));
        assertFalse(result.contains("buy milk"));
        assertEquals("No matching tasks found.", kairo.getResponse("find missing"));
        assertEquals(before, kairo.getResponse("list"));
    }

    /**
     * Checks that invalid input returns errors and the next command still works.
     */
    @Test
    void getResponse_invalidCommands_returnsErrorsAndContinues() {
        Kairo kairo = new Kairo(temporaryDirectory.resolve("kairo.txt"));
        for (String command : List.of(
                "", "unknown", "todo", "find", "mark abc", "mark 0",
                "mark 99999999999999999999", "unmark 1", "delete 1",
                "deadline report", "deadline report /by 2026-02-30",
                "event meeting /from 2026-09-16",
                "event meeting /from invalid /to 2026-09-17")) {
            assertTrue(kairo.getResponse(command).startsWith("OOPS!"), command);
        }
        assertEquals("Your task list is empty.", kairo.getResponse("list"));
        assertTrue(kairo.getResponse("todo continue").contains("[T][ ] continue"));
    }

    /**
     * Checks that a load error is included in the opening response.
     *
     * @throws IOException If the temporary file cannot be written.
     */
    @Test
    void getWelcomeMessage_invalidStorage_includesLoadError() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Files.writeString(file, "invalid saved data\n");
        Kairo kairo = new Kairo(file);
        assertTrue(kairo.getWelcomeMessage().startsWith("Hello! I'm Kairo."));
        assertTrue(kairo.getWelcomeMessage().contains("OOPS!"));
    }

    /**
     * Checks that a save failure reaches the UI as an error response.
     *
     * @throws IOException If the temporary file cannot be written.
     */
    @Test
    void getResponse_unwritableStorage_returnsSaveError() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "occupied");
        Kairo kairo = new Kairo(parentFile.resolve("kairo.txt"));
        assertEquals("OOPS! I could not save your tasks.", kairo.getResponse("todo read book"));
    }

    /**
     * Checks that bye returns a farewell and stops further command processing.
     */
    @Test
    void getResponse_bye_setsExitFlag() {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Kairo kairo = new Kairo(file);
        assertFalse(kairo.isExit());
        assertEquals("Bye. Hope to see you again soon!", kairo.getResponse("bye"));
        assertTrue(kairo.isExit());
        kairo.getResponse("todo should not be added");
        assertFalse(Files.exists(file));
    }
}
