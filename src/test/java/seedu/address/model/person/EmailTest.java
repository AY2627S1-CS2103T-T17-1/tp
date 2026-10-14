package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the MVP email grammar, whitespace rejection, and case-insensitive identity comparison.
 */
public class EmailTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
    }

    @Test
    public void isValidEmail() {
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));
        String[] invalidEmails = {"", " ", "@example.com", "alexexample.com", "alex@", "alex@localhost",
            "alex@@example.com", "alex@.example.com", "alex@example.com.", "alex@example..com",
            "alex@-example.com", "alex@example.com-", "alex@exam_ple.com", "alex tan@example.com",
            "alex@example com", "alex\t@example.com", "alex\u00a0tan@example.com"};
        for (String email : invalidEmails) {
            assertFalse(Email.isValidEmail(email), email);
        }
        String[] validEmails = {"Alex@example.com", "a@b.c", "-alex@example.com", "alex-@example.com",
            "alex..tan@example.com", "alex+lesson@u.nus.edu", "  alex@example.com  "};
        for (String email : validEmails) {
            assertTrue(Email.isValidEmail(email), email);
        }
        assertEquals("Alex@Example.com", new Email("  Alex@Example.com  ").value);
    }

    @Test
    public void isSameEmail() {
        Email email = new Email("Alice@example.com");
        assertTrue(email.isSameEmail(new Email("alice@EXAMPLE.COM")));
        assertFalse(email.isSameEmail(new Email("bob@example.com")));
        assertThrows(NullPointerException.class, () -> email.isSameEmail(null));
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email.com");
        assertTrue(email.equals(new Email("valid@email.com")));
        assertTrue(email.equals(email));
        assertFalse(email.equals(null));
        assertFalse(email.equals(5.0f));
        assertFalse(email.equals(new Email("other.valid@email.com")));
    }
}
