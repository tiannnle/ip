package kairo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests invalid input and storage failures through the public command interface.
 */
class ErrorHandlingTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_emptyOrMultilineInput_returnsErrorWithoutSaving() {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Kairo kairo = new Kairo(file);
        assertEquals("OOPS! Please enter a command.", kairo.getResponse(null));
        assertEquals("OOPS! Please enter a command.", kairo.getResponse(" \t "));
        assertEquals("OOPS! Please enter one command at a time.",
                kairo.getResponse("todo first\ntodo second"));
        assertEquals("OOPS! Please enter one command at a time.",
                kairo.getResponse("todo first\rsecond"));
        assertFalse(Files.exists(file));
        assertEquals("Your task list is empty.", kairo.getResponse("list"));
    }

    @Test
    void getResponse_extraListOrByeArguments_rejectsAndKeepsSessionOpen() {
        Kairo kairo = new Kairo(temporaryDirectory.resolve("kairo.txt"));
        assertEquals("OOPS! Use: list (without extra arguments)", kairo.getResponse("list 1"));
        assertEquals("OOPS! Use: bye (without extra arguments)", kairo.getResponse("bye now"));
        assertFalse(kairo.isExit());
        assertTrue(kairo.getResponse("todo continue").contains("[T][ ] continue"));
        assertEquals("Bye. Hope to see you again soon!", kairo.getResponse("  bye  "));
        assertTrue(kairo.isExit());
    }

    @Test
    void parseDateCommands_spacesAndTabs_preservesDescription() throws KairoException {
        Deadline deadline = Parser.parseDeadline("deadline  team   report \t/by\t 2026-09-18");
        Event event = Parser.parseEvent("event  team   meeting\t/from\t2026-09-18  /to  2026-09-19");
        assertEquals("D | 0 | team   report | 2026-09-18", deadline.toDataString());
        assertEquals("E | 0 | team   meeting | 2026-09-18 | 2026-09-19", event.toDataString());
    }

    @Test
    void parseDeadline_invalidMarkersOrMissingFields_rejectsCommand() {
        for (String input : List.of(
                "deadline /by 2026-09-18", "deadline report /by", "deadline report /by   ",
                "deadline report /by 2026-09-18 /by 2026-09-19",
                "deadline report /from 2026-09-18 /by 2026-09-19",
                "deadline report/by 2026-09-18", "deadline report /by2026-09-18")) {
            KairoException error = assertThrows(KairoException.class, () -> Parser.parseDeadline(input));
            assertEquals("Use: deadline DESCRIPTION /by DATE", error.getMessage());
        }
    }

    @Test
    void parseEvent_invalidMarkersOrMissingFields_rejectsCommand() {
        for (String input : List.of(
                "event /from 2026-09-18 /to 2026-09-19",
                "event meeting /from /to 2026-09-19",
                "event meeting /from 2026-09-18 /to",
                "event meeting /to 2026-09-19 /from 2026-09-18",
                "event meeting /from /from 2026-09-18 /to 2026-09-19",
                "event meeting /from 2026-09-18 /to 2026-09-19 /to 2026-09-20",
                "event meeting /by 2026-09-18 /from 2026-09-18 /to 2026-09-19")) {
            KairoException error = assertThrows(KairoException.class, () -> Parser.parseEvent(input));
            assertEquals("Use: event DESCRIPTION /from START /to END", error.getMessage());
        }
    }

    @Test
    void parseEvent_endBeforeStart_rejectsButAllowsSameDay() throws KairoException {
        KairoException error = assertThrows(KairoException.class,
                () -> Parser.parseEvent("event meeting /from 2026-09-19 /to 2026-09-18"));
        assertEquals("The event end date cannot be before its start date.", error.getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "2026-09-19", "2026-09-18"));
        assertEquals("E | 0 | meeting | 2026-09-18 | 2026-09-18",
                Parser.parseEvent("event meeting /from 2026-09-18 /to 2026-09-18").toDataString());
    }

    @Test
    void parseDateCommands_invalidCalendarDates_rejectsButAllowsLeapDay() throws KairoException {
        for (String date : List.of("2026-02-29", "2026-02-30", "2026-13-01", "18-09-2026")) {
            assertThrows(KairoException.class, () -> Parser.parseDeadline("deadline report /by " + date));
            assertThrows(KairoException.class,
                    () -> Parser.parseEvent("event meeting /from " + date + " /to 2026-12-31"));
        }
        assertEquals("D | 0 | report | 2024-02-29",
                Parser.parseDeadline("deadline report /by 2024-02-29").toDataString());
    }

    @Test
    void getResponse_pipeInDescription_rejectsWithoutWritingInvalidData() {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Kairo kairo = new Kairo(file);
        for (String command : List.of("todo read | write", "todo read|write",
                "deadline read | write /by 2026-09-18",
                "event read | write /from 2026-09-18 /to 2026-09-19")) {
            assertEquals("OOPS! Please remove the pipe character (|) from the task description.",
                    kairo.getResponse(command));
        }
        assertFalse(Files.exists(file));
        assertEquals("Your task list is empty.", kairo.getResponse("list"));
    }

    @Test
    void getResponse_invalidSavedData_reportsLineAndPreventsOverwrite() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        for (String invalidLine : List.of("broken", "T | 2 | task", "T | 0 | ",
                "D | 0 | report | 2026-02-30", "T | 0 | task | extra",
                "E | 0 | meeting | 2026-09-19 | 2026-09-18", "X | 0 | task")) {
            String savedData = "T | 0 | keep this task\n" + invalidLine + "\n";
            Files.writeString(file, savedData);
            Kairo kairo = new Kairo(file);
            assertTrue(kairo.getWelcomeMessage().contains("Invalid saved task at line 2"));
            for (String command : List.of("todo new task", "mark 1", "unmark 1", "delete 1",
                    "deadline report /by 2026-09-18", "sort name", "sort date", "list", "find keep")) {
                assertEquals("OOPS! Your saved tasks could not be loaded. "
                        + "Fix the task file and restart Kairo.", kairo.getResponse(command));
                assertEquals(savedData, Files.readString(file));
            }
            assertEquals("Bye. Hope to see you again soon!", kairo.getResponse("bye"));
        }
    }

    @Test
    void getResponse_saveFails_keepsTasksStatusAndOrderUntilRetry() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Path backup = temporaryDirectory.resolve("before-save.txt");
        Kairo kairo = new Kairo(file);
        kairo.getResponse("todo Zebra");
        kairo.getResponse("deadline Alpha /by 2026-09-18");
        kairo.getResponse("mark 2");
        String beforeList = kairo.getResponse("list");
        String beforeData = Files.readString(file);

        // A directory at the file path reliably forces a save failure on all operating systems.
        Files.move(file, backup);
        Files.createDirectory(file);
        Path blocker = file.resolve("keep.txt");
        Files.writeString(blocker, "keep");
        for (String command : List.of("todo new task", "deadline new /by 2026-09-19",
                "event new /from 2026-09-19 /to 2026-09-20", "delete 1",
                "mark 1", "unmark 2", "mark 2", "unmark 1", "sort name", "sort date")) {
            assertEquals("OOPS! I could not save your tasks.", kairo.getResponse(command));
            assertEquals(beforeList, kairo.getResponse("list"));
            assertEquals(beforeData, Files.readString(backup));
            assertEquals("keep", Files.readString(blocker));
        }

        Files.delete(blocker);
        Files.delete(file);
        Files.move(backup, file);
        assertTrue(kairo.getResponse("todo try again").contains("[T][ ] try again"));
        assertEquals(kairo.getResponse("list"), new Kairo(file).getResponse("list"));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(0L, files.filter(path -> path.getFileName().toString().endsWith(".tmp")).count());
        }
    }

    @Test
    void getResponse_missingDataFolder_createsStorageOnFirstSuccessfulCommand() throws IOException {
        Path file = temporaryDirectory.resolve("data/kairo.txt");
        Kairo kairo = new Kairo(file);
        assertEquals("Your task list is empty.", kairo.getResponse("list"));
        assertFalse(Files.exists(file));
        assertTrue(kairo.getResponse("todo start").contains("[T][ ] start"));
        assertEquals(List.of("T | 0 | start"), Files.readAllLines(file));
    }

    @Test
    void getResponse_unreadableDataLocation_reportsLoadErrorWithoutReplacingDirectory() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Files.createDirectory(file);
        Kairo kairo = new Kairo(file);
        assertTrue(kairo.getWelcomeMessage().contains("I could not load your saved tasks."));
        assertTrue(kairo.getResponse("todo start").contains("Fix the task file and restart Kairo."));
        assertTrue(Files.isDirectory(file));
    }

    @Test
    void getResponse_repairedStorage_loadsOnRestart() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Files.writeString(file, "damaged\n");
        Kairo oldSession = new Kairo(file);
        Files.writeString(file, "T | 1 | keep old A|B text\n");
        assertTrue(oldSession.getResponse("todo wait").contains("restart Kairo"));
        Kairo restarted = new Kairo(file);
        assertTrue(restarted.getResponse("list").contains("[T][X] keep old A|B text"));
        assertTrue(restarted.getResponse("todo continue").contains("[T][ ] continue"));
        assertEquals(2, Files.readAllLines(file).size());
    }
}
