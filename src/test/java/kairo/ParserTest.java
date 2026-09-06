package kairo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests command validation performed by Parser.
 */
class ParserTest {

    /**
     * Tests that a valid task number is converted to a zero-based index.
     *
     * @throws KairoException if parsing unexpectedly fails
     */
    @Test
    void parseTaskIndex_validNumber_returnsZeroBasedIndex()
            throws KairoException {
        int result = Parser.parseTaskIndex("mark 2", "mark", 3);

        assertEquals(1, result);
    }

    /**
     * Tests that a missing task number is rejected.
     */
    @Test
    void parseTaskIndex_missingNumber_throwsException() {
        assertThrows(
                KairoException.class,
                () -> Parser.parseTaskIndex("mark", "mark", 3));
    }

    /**
     * Tests that a non-numeric task number is rejected.
     */
    @Test
    void parseTaskIndex_nonNumericNumber_throwsException() {
        assertThrows(
                KairoException.class,
                () -> Parser.parseTaskIndex("mark abc", "mark", 3));
    }

    /**
     * Tests that a task number outside the list is rejected.
     */
    @Test
    void parseTaskIndex_outOfRangeNumber_throwsException() {
        assertThrows(
                KairoException.class,
                () -> Parser.parseTaskIndex("mark 4", "mark", 3));
    }
}