package kairo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests sorting, command validation, and persistence of the new task order.
 */
class SortTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void parseSortKey_validFields_returnsField() throws KairoException {
        assertEquals(CommandType.SORT, Parser.parseCommandType("sort name"));
        assertEquals("name", Parser.parseSortKey("sort name"));
        assertEquals("date", Parser.parseSortKey("  sort\tdate  "));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("sortname"));
    }

    @Test
    void parseSortKey_invalidArguments_throwsException() {
        for (String input : List.of("", "sort", "sort priority", "sort NAME",
                "sort name extra", "sort date extra", "list name")) {
            KairoException exception = assertThrows(
                    KairoException.class, () -> Parser.parseSortKey(input), input);
            assertEquals("Use: sort name OR sort date", exception.getMessage());
        }
    }

    @Test
    void sortByName_mixedTypesAndEqualNames_preservesTaskDetailsAndTieOrder() {
        Task zulu = new Todo("Zulu");
        Task alpha = new Deadline("alpha", "2026-10-10");
        Task beta = new Event("Beta", "2026-09-15", "2026-09-16");
        Task sameName = new Todo("ALPHA");
        alpha.markAsDone();
        TaskList list = new TaskList(new ArrayList<>(List.of(zulu, alpha, beta, sameName)));

        list.sortByName();

        assertEquals(List.of(alpha, sameName, beta, zulu), list.getTasks());
        assertEquals("D | 1 | alpha | 2026-10-10", alpha.toDataString());
        assertEquals("E | 0 | Beta | 2026-09-15 | 2026-09-16", beta.toDataString());
        list.sortByName();
        assertEquals(List.of(alpha, sameName, beta, zulu), list.getTasks());
    }

    @Test
    void sortByDate_mixedDates_ordersChronologicallyWithTodosLast() {
        Task firstTodo = new Todo("first todo");
        Task lateDeadline = new Deadline("late", "2027-01-01");
        Task tiedEvent = new Event("tie event", "2026-10-02", "2026-10-05");
        Task secondTodo = new Todo("second todo");
        Task tiedDeadline = new Deadline("tie deadline", "2026-10-02");
        Task earlierEvent = new Event("earlier event", "2025-12-30", "2027-02-01");
        Task earliestDeadline = new Deadline("earliest", "2025-02-01");
        TaskList list = new TaskList(new ArrayList<>(List.of(firstTodo, lateDeadline,
                tiedEvent, secondTodo, tiedDeadline, earlierEvent, earliestDeadline)));

        list.sortByDate();

        assertEquals(List.of(earliestDeadline, earlierEvent, tiedEvent, tiedDeadline,
                lateDeadline, firstTodo, secondTodo), list.getTasks());
    }

    @Test
    void sort_emptyAndSingleTaskLists_keepsValidContents() {
        TaskList list = new TaskList();
        list.sortByName();
        list.sortByDate();
        assertTrue(list.getTasks().isEmpty());

        Task todo = new Todo("only task");
        list.add(todo);
        list.sortByName();
        list.sortByDate();
        assertEquals(List.of(todo), list.getTasks());
    }

    @Test
    void sortByDate_latestPossibleDate_stillPlacesTodoLast() {
        Task todo = new Todo("no date");
        Task deadline = new Deadline("last date", "+999999999-12-31");
        TaskList list = new TaskList(new ArrayList<>(List.of(todo, deadline)));

        list.sortByDate();

        assertEquals(List.of(deadline, todo), list.getTasks());
    }

    @Test
    void getResponse_sortName_savesOrderAndReloadsIt() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Kairo kairo = new Kairo(file);
        kairo.getResponse("todo Zulu");
        kairo.getResponse("deadline alpha /by 2026-10-10");
        kairo.getResponse("event Beta /from 2026-09-15 /to 2026-09-16");
        kairo.getResponse("todo ALPHA");
        kairo.getResponse("mark 2");

        String response = kairo.getResponse("  sort   name  ");

        assertEquals(kairo.getResponse("list"), response);
        assertEquals(List.of("D | 1 | alpha | 2026-10-10", "T | 0 | ALPHA",
                "E | 0 | Beta | 2026-09-15 | 2026-09-16", "T | 0 | Zulu"),
                Files.readAllLines(file));
        assertEquals(response, new Kairo(file).getResponse("list"));
    }

    @Test
    void getResponse_sortDate_savesOrderAndUpdatesTaskNumbers() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Kairo kairo = new Kairo(file);
        kairo.getResponse("todo Zulu");
        kairo.getResponse("deadline report /by 2026-10-01");
        kairo.getResponse("event meeting /from 2026-09-15 /to 2026-12-01");

        String response = kairo.getResponse("sort date");

        assertEquals(List.of("E | 0 | meeting | 2026-09-15 | 2026-12-01",
                "D | 0 | report | 2026-10-01", "T | 0 | Zulu"), Files.readAllLines(file));
        assertEquals(response, new Kairo(file).getResponse("list"));
        assertTrue(kairo.getResponse("mark 1").contains("[E][X] meeting"));
        assertTrue(kairo.getResponse("delete 2").contains("[D][ ] report"));
        assertEquals(List.of("E | 1 | meeting | 2026-09-15 | 2026-12-01", "T | 0 | Zulu"),
                Files.readAllLines(file));
    }

    @Test
    void getResponse_invalidSort_keepsStoredAndActiveOrder() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Kairo kairo = new Kairo(file);
        kairo.getResponse("todo Zulu");
        kairo.getResponse("todo alpha");
        String before = kairo.getResponse("list");
        String saved = Files.readString(file);

        for (String command : List.of("sort", "sort priority", "sort name extra", "sort NAME")) {
            assertEquals("OOPS! Use: sort name OR sort date", kairo.getResponse(command));
            assertEquals(before, kairo.getResponse("list"));
            assertEquals(saved, Files.readString(file));
        }
    }

    @Test
    void getResponse_sortSaveFailure_keepsActiveOrder() throws IOException {
        Path file = temporaryDirectory.resolve("kairo.txt");
        Kairo kairo = new Kairo(file);
        kairo.getResponse("todo Zulu");
        kairo.getResponse("todo alpha");
        String before = kairo.getResponse("list");
        Files.delete(file);
        Files.createDirectory(file);

        assertEquals("OOPS! I could not save your tasks.", kairo.getResponse("sort name"));
        assertEquals(before, kairo.getResponse("list"));

        Files.delete(file);
        String response = kairo.getResponse("sort name");
        assertEquals(response, new Kairo(file).getResponse("list"));
    }
}
