package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests academic-level boundaries, canonicalization, and progression.
 */
public class LevelTest {
    /**
     * Checks that construction rejects null while the validation predicate returns false.
     */
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Level(null));
        assertFalse(Level.isValidLevel(null));
    }

    /**
     * Checks both ends of each supported academic stage.
     */
    @Test
    public void constructor_supportedStageBoundaries_storesCanonicalValues() {
        for (String valid : List.of("Primary 1", "Primary 6", "Secondary 1", "Secondary 5", "JC 1", "JC 2")) {
            assertTrue(Level.isValidLevel(valid));
            assertEquals(valid, new Level(valid).value);
        }
    }

    /**
     * Checks rejection of unsupported stages, out-of-range years, and malformed labels.
     */
    @Test
    public void constructor_invalidLevel_throwsIllegalArgumentException() {
        for (String invalid : List.of("", " ", "Primary 0", "Primary 7", "Secondary 0", "Secondary 6",
                "JC 0", "JC 3", "JC 5", "Adult", "O-Level", "P6", "Secondary 03", "JC2",
                "Secondary-3", "Secondary 3/4", "Secondary 3 extra", "Primary 6!", "a".repeat(31))) {
            assertFalse(Level.isValidLevel(invalid));
            assertThrows(IllegalArgumentException.class, Level.MESSAGE_CONSTRAINTS, () -> new Level(invalid));
        }
    }

    /**
     * Checks canonical spelling and equality after case and space normalization.
     */
    @Test
    public void constructor_mixedCaseAndSpaces_storesCanonicalLevel() {
        assertEquals("Secondary 3", new Level("  sEcOnDaRy   3  ").value);
        assertEquals("JC 2", new Level("jc 2").value);
        assertEquals(new Level("Primary 6"), new Level("primary 6"));
        assertEquals(new Level("JC 1").hashCode(), new Level("jc 1").hashCode());
    }

    /**
     * Checks the complete progression across primary, secondary, and junior-college levels.
     */
    @Test
    public void getSortRank_allSupportedLevels_followsAcademicProgression() {
        List<String> levels = List.of("Primary 1", "Primary 2", "Primary 3", "Primary 4", "Primary 5",
                "Primary 6", "Secondary 1", "Secondary 2", "Secondary 3", "Secondary 4", "Secondary 5",
                "JC 1", "JC 2");
        for (int rank = 0; rank < levels.size(); rank++) {
            assertEquals(rank, new Level(levels.get(rank)).getSortRank());
        }
    }

    /**
     * Checks value-based equality and the canonical display text.
     */
    @Test
    public void equals() {
        Level level = new Level("Secondary 3");
        assertEquals("Secondary 3", level.toString());
        assertTrue(level.equals(level));
        assertFalse(level.equals(null));
        assertFalse(level.equals("Secondary 3"));
        assertFalse(level.equals(new Level("Secondary 4")));
    }
}
