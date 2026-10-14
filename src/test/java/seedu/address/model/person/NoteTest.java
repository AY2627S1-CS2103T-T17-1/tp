package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contains tests for {@link Note}.
 */
public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void constructor_invalidNote_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Note(""));
        assertThrows(IllegalArgumentException.class, () -> new Note("First line\nSecond line"));
        assertThrows(IllegalArgumentException.class, () -> new Note("a".repeat(201)));
    }

    @Test
    public void constructor_validNote_trimsValue() {
        assertEquals("Contact after 6 pm", new Note("  Contact after 6 pm  ").value);
    }

    @Test
    public void isValidNote() {
        assertFalse(Note.isValidNote(null));
        assertFalse(Note.isValidNote(""));
        assertFalse(Note.isValidNote("   "));
        assertFalse(Note.isValidNote("First line\rSecond line"));

        assertTrue(Note.isValidNote("Prefers visual examples."));
        assertTrue(Note.isValidNote("  Contact after 6 pm  "));
        assertTrue(Note.isValidNote("a".repeat(200)));
    }

    @Test
    public void equals() {
        Note note = new Note("Contact after 6 pm");

        assertTrue(note.equals(new Note("Contact after 6 pm")));
        assertTrue(note.equals(note));
        assertFalse(note.equals(null));
        assertFalse(note.equals("Contact after 6 pm"));
        assertFalse(note.equals(new Note("Prefers email")));
    }
}
