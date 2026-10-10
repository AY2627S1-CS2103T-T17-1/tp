package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests role validation, parsing, and canonical formatting.
 */
public class RoleTest {

    @Test
    public void isValidRole() {
        assertTrue(Role.isValidRole("student"));
        assertTrue(Role.isValidRole("PARENT"));
        assertFalse(Role.isValidRole("teacher"));
        assertFalse(Role.isValidRole(""));
        assertFalse(Role.isValidRole(null));
    }

    @Test
    public void fromString_validRole_returnsCanonicalRole() {
        assertEquals(Role.STUDENT, Role.fromString("STUDENT"));
        assertEquals(Role.PARENT, Role.fromString("parent"));
    }

    @Test
    public void fromString_invalidRole_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Role.MESSAGE_CONSTRAINTS, () ->
                Role.fromString("teacher"));
    }

    @Test
    public void toStringMethod_returnsLowercaseRole() {
        assertEquals("student", Role.STUDENT.toString());
        assertEquals("parent", Role.PARENT.toString());
    }
}
