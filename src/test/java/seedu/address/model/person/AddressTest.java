package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests address content categories, trimming, and length limits.
 */
public class AddressTest {

    /**
     * Checks that spaces, formatting, or combining marks cannot supply the required address content alone.
     */
    @Test
    public void isValidAddress_noContentCharacter_returnsFalse() {
        String[] addresses = {"\u200B", "\u200D", "\uFEFF", "\u00A0", "\u2003", "\u0301", "\uFE0F",
            " \u200B\u200D\u00A0\u0301 "};
        for (String address : addresses) {
            assertFalse(Address.isValidAddress(address));
            assertThrows(IllegalArgumentException.class, Address.MESSAGE_CONSTRAINTS, () -> new Address(address));
        }
    }

    /**
     * Checks that Unicode content and accompanying marks or formatting are preserved without substitution.
     */
    @Test
    public void constructor_unicodeContent_preservesCharacters() {
        String[] addresses = {"新加坡 1号", "شارع\u200C 12", "Cafe\u0301 Road", "1\u200BRoad", "-", "£", "½",
            "\uD83C\uDFE0", "1\u2003Road"};
        for (String address : addresses) {
            assertTrue(Address.isValidAddress(address));
            assertEquals(address, new Address("  " + address + "  ").value);
        }
    }

    /**
     * Checks the documented category boundary even for letters and symbols that can appear blank in a font.
     */
    @Test
    public void isValidAddress_letterAndSymbolFillers_followCategoryRule() {
        assertTrue(Address.isValidAddress("\u3164"));
        assertTrue(Address.isValidAddress("\u2800"));
    }

    /**
     * Checks unchanged length limits and rejection of internal controls and line separators.
     */
    @Test
    public void constructor_printableAddress_enforcesBoundaries() {
        assertEquals("1 Road", new Address("  1 Road  ").value);
        assertTrue(Address.isValidAddress("a".repeat(150)));
        assertFalse(Address.isValidAddress("a".repeat(151)));
        assertTrue(Address.isValidAddress("a" + "\u0301".repeat(149)));
        assertFalse(Address.isValidAddress("a" + "\u0301".repeat(150)));
        assertTrue(Address.isValidAddress("\uD83C\uDFE0".repeat(150)));
        assertFalse(Address.isValidAddress("\uD83C\uDFE0".repeat(151)));
        assertFalse(Address.isValidAddress("1 Road\nUnit 2"));
        assertFalse(Address.isValidAddress("1 Road\tUnit 2"));
        assertFalse(Address.isValidAddress("1 Road\u2028Unit 2"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Address(null));
    }

    @Test
    public void constructor_invalidAddress_throwsIllegalArgumentException() {
        String invalidAddress = "";
        assertThrows(IllegalArgumentException.class, () -> new Address(invalidAddress));
    }

    @Test
    public void isValidAddress() {
        // null address
        assertThrows(NullPointerException.class, () -> Address.isValidAddress(null));

        // invalid addresses
        assertFalse(Address.isValidAddress("")); // empty string
        assertFalse(Address.isValidAddress(" ")); // spaces only

        // valid addresses
        assertTrue(Address.isValidAddress("Blk 456, Den Road, #01-355"));
        assertTrue(Address.isValidAddress("-")); // one character
        assertTrue(Address.isValidAddress("Leng Inc; 1234 Market St; San Francisco CA 2349879; USA")); // long address
    }

    @Test
    public void equals() {
        Address address = new Address("Valid Address");

        // same values -> returns true
        assertTrue(address.equals(new Address("Valid Address")));

        // same object -> returns true
        assertTrue(address.equals(address));

        // null -> returns false
        assertFalse(address.equals(null));

        // different types -> returns false
        assertFalse(address.equals(5.0f));

        // different values -> returns false
        assertFalse(address.equals(new Address("Other Valid Address")));
    }
}
