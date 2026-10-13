package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contains tests for {@link Subject}.
 */
public class SubjectTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Subject(null));
    }

    @Test
    public void constructor_invalidSubject_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Subject(""));
        assertThrows(IllegalArgumentException.class, () -> new Subject("Music: Theory"));
        assertThrows(IllegalArgumentException.class, () -> new Subject("a".repeat(41)));
    }

    @Test
    public void constructor_validSubject_normalizesSpaces() {
        assertEquals("A-Math / Physics", new Subject("  A-Math  /  Physics  ").value);
    }

    @Test
    public void isValidSubject() {
        assertFalse(Subject.isValidSubject(null));
        assertFalse(Subject.isValidSubject(""));
        assertFalse(Subject.isValidSubject("   "));
        assertFalse(Subject.isValidSubject("Music: Theory"));

        assertTrue(Subject.isValidSubject("English"));
        assertTrue(Subject.isValidSubject("  A-Math  "));
        assertTrue(Subject.isValidSubject("A-Math & Physics+ / (Express), Sec. 3"));
    }

    @Test
    public void equals() {
        Subject subject = new Subject("Mathematics");

        assertTrue(subject.equals(new Subject("Mathematics")));
        assertTrue(subject.equals(subject));
        assertFalse(subject.equals(null));
        assertFalse(subject.equals("Mathematics"));
        assertFalse(subject.equals(new Subject("Physics")));
    }
}
