package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contains tests for {@link Level}.
 */
public class LevelTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Level(null));
    }

    @Test
    public void constructor_invalidLevel_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Level(""));
        assertThrows(IllegalArgumentException.class, () -> new Level("Secondary 3/4"));
        assertThrows(IllegalArgumentException.class, () -> new Level("a".repeat(31)));
    }

    @Test
    public void constructor_validLevel_normalizesSpaces() {
        assertEquals("Secondary 3", new Level("  Secondary  3  ").value);
    }

    @Test
    public void isValidLevel() {
        assertFalse(Level.isValidLevel(null));
        assertFalse(Level.isValidLevel(""));
        assertFalse(Level.isValidLevel("   "));
        assertFalse(Level.isValidLevel("Primary 6!"));

        assertTrue(Level.isValidLevel("Primary 6"));
        assertTrue(Level.isValidLevel("  Secondary  3  "));
        assertTrue(Level.isValidLevel("Adult"));
        assertTrue(Level.isValidLevel("O-Level"));
    }

    @Test
    public void equals() {
        Level level = new Level("Secondary 3");

        assertTrue(level.equals(new Level("Secondary 3")));
        assertTrue(level.equals(level));
        assertFalse(level.equals(null));
        assertFalse(level.equals("Secondary 3"));
        assertFalse(level.equals(new Level("Secondary 4")));
    }
}
