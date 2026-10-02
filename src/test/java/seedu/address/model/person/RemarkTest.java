package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests unrestricted remark values and value equality.
 */
public class RemarkTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_unrestrictedText_preservesValue() {
        for (String text : new String[] {"", " ", "Likes swimming", "泳ぐ!\nCall tomorrow."}) {
            assertEquals(text, new Remark(text).value);
            assertEquals(text, new Remark(text).toString());
        }
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Likes swimming");
        assertTrue(remark.equals(remark));
        assertTrue(remark.equals(new Remark("Likes swimming")));
        assertFalse(remark.equals(null));
        assertFalse(remark.equals("Likes swimming"));
        assertFalse(remark.equals(new Remark("Likes running")));
        assertEquals(remark.hashCode(), new Remark("Likes swimming").hashCode());
    }
}
