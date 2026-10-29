package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
        assertTrue(Tag.isValidTagName("priority1"));
        assertTrue(Tag.isValidTagName("A".repeat(1000)));
        assertFalse(Tag.isValidTagName(""));
        assertFalse(Tag.isValidTagName("priority-1"));
        assertFalse(Tag.isValidTagName("Café"));
        assertFalse(Tag.isValidTagName("数学"));
    }

}
