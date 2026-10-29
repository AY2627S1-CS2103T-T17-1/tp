package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests normalized name values, permitted punctuation, limits, and duplicate identity.
 */
public class NameTest {

    @Test
    public void constructor_normalizesAndEnforcesBoundaries() {
        assertEquals("Zoë O'Neil-Tan Jr.", new Name("  Zoë   O'Neil-Tan Jr.  ").fullName);
        assertTrue(Name.isValidName("A"));
        assertTrue(Name.isValidName("A".repeat(70)));
        assertFalse(Name.isValidName("A".repeat(71)));
        assertFalse(Name.isValidName("-Alex"));
        assertFalse(Name.isValidName("Alex\nTan"));
    }

    @Test
    public void constructor_decomposedAccents_storesPrecomposedForm() {
        String decomposed = "Sine\u0301ad Ni\u0301 Bhriain";
        String precomposed = "Sin\u00e9ad N\u00ed Bhriain";

        Name name = new Name(decomposed);

        assertEquals(precomposed, name.fullName);
        assertEquals(new Name(precomposed), name);
        assertTrue(name.isSameName(new Name(precomposed)));
    }

    @Test
    public void isValidName_combiningMarks() {
        // scripts whose vowel signs and diacritics stay as separate combining marks
        assertTrue(Name.isValidName("\u0b95\u0ba9\u0bbf\u0bae\u0bca\u0bb4\u0bbf \u0b85\u0bb0\u0bc1\u0ba3\u0bcd"));
        assertTrue(Name.isValidName("\u0906\u0930\u0935 \u0936\u0930\u094d\u092e\u093e"));
        assertTrue(Name.isValidName("\u0645\u064f\u062d\u064e\u0645\u0651\u064e\u062f \u0639\u0644\u064a"));

        // a combining mark must follow a letter or digit
        assertFalse(Name.isValidName("\u0301Alex"));
        assertFalse(Name.isValidName("Alex \u0301Tan"));
        assertFalse(Name.isValidName("O'\u0301Neil"));
    }

    @Test
    public void isValidName_supplementaryLetters_countsCodePoints() {
        String supplementaryLetter = "\uD840\uDC00"; // U+20000, one letter stored as two UTF-16 units
        assertTrue(Name.isValidName(supplementaryLetter.repeat(70)));
        assertFalse(Name.isValidName(supplementaryLetter.repeat(71)));
    }

    @Test
    public void isValidName_restrictedPunctuation() {
        assertTrue(Name.isValidName("Mary Anne D'Souza")); // straight apostrophe
        assertFalse(Name.isValidName("Niamh O\u2019Connor")); // curly apostrophe
        assertFalse(Name.isValidName("Ravi s/o Kumar")); // slash
        assertTrue(Name.isValidName("Ravi s.o. Kumar")); // suggested workaround
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }

    @Test
    public void isSameName() {
        Name name = new Name("Alice Pauline");

        assertTrue(name.isSameName(new Name("Alice  Pauline")));
        assertTrue(name.isSameName(new Name("alice Pauline")));
        assertThrows(NullPointerException.class, () -> name.isSameName(null));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
