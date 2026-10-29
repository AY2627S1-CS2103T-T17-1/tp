package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Subject;
import seedu.address.model.tag.Tag;

/**
 * Tests conversion of user input into validated model fields.
 */
public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65-";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";

    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final String VALID_ROLE = "student";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseStudentFields_normalizesAndRejectsInvalidValues() throws ParseException {
        assertEquals("A-Math", ParserUtil.parseSubject("  A-Math  ").value);
        assertEquals("Secondary 3", ParserUtil.parseLevel(" Secondary   3 ").value);
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSubject(null));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseLevel(null));
        assertThrows(ParseException.class, Subject.MESSAGE_CONSTRAINTS, () -> {
            ParserUtil.parseSubject("s".repeat(41));
        });
        assertThrows(ParseException.class, Level.MESSAGE_CONSTRAINTS, () -> {
            ParserUtil.parseLevel("l".repeat(31));
        });
    }

    @Test
    public void parseNote_optionalAndNonemptyValues_followConstraints() throws ParseException {
        assertTrue(ParserUtil.parseNote("").isEmpty());
        assertTrue(ParserUtil.parseNote("  ").isEmpty());
        assertEquals("Contact after 6 pm", ParserUtil.parseNote(" Contact after 6 pm ").orElseThrow().value);
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNote(null));
        assertThrows(ParseException.class, Note.MESSAGE_CONSTRAINTS, () -> {
            ParserUtil.parseNote("n".repeat(201));
        });
        assertThrows(ParseException.class, Note.MESSAGE_CONSTRAINTS, () -> {
            ParserUtil.parseNote("First line\nSecond line");
        });
    }

    /**
     * Checks that Unicode-only whitespace omits a note and nonblank notes share model normalization.
     *
     * @throws ParseException If a supported note unexpectedly fails validation.
     */
    @Test
    public void parseNote_unicodeWhitespace_omitsBlankAndNormalizesContent() throws ParseException {
        assertTrue(ParserUtil.parseNote("\u2003").isEmpty());
        assertTrue(ParserUtil.parseNote(" \t\u2002\u3000 ").isEmpty());
        assertEquals("After\u20036 pm", ParserUtil.parseNote("\u2003After\u20036 pm\u3000")
                .orElseThrow().value);
        assertEquals("\u00a0", ParserUtil.parseNote("\u00a0").orElseThrow().value);
    }

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseDisplayedIndex_validInput_returnsIndex() throws Exception {
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseDisplayedIndex("1"));
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseDisplayedIndex("0001"));
        assertEquals(Index.fromOneBased(Integer.MAX_VALUE), ParserUtil.parseDisplayedIndex("2147483647"));
    }

    @Test
    public void parseDisplayedIndex_notPositiveInteger_throwsParseException() {
        for (String invalidIndex : List.of("0", "0000", "-1", "+1", "1.5", "1a", "abc", "", " 1")) {
            assertThrows(ParseException.class, Messages.MESSAGE_INVALID_DISPLAYED_INDEX, () ->
                    ParserUtil.parseDisplayedIndex(invalidIndex));
        }
    }

    @Test
    public void parseDisplayedIndex_tooLargeForInt_throwsParseException() {
        assertThrows(ParseException.class, "No contact exists at index 2147483648 in the current list.", () ->
                ParserUtil.parseDisplayedIndex("2147483648"));
        assertThrows(ParseException.class,
                "No contact exists at index 99999999999999999999 in the current list.", () ->
                ParserUtil.parseDisplayedIndex("99999999999999999999"));
    }

    @Test
    public void parseDisplayedIndex_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseDisplayedIndex(null));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseRole_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRole(null));
    }

    @Test
    public void parseRole_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Role.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseRole("teacher"));
    }

    @Test
    public void parseRole_validValue_returnsRole() throws Exception {
        assertEquals(Role.STUDENT, ParserUtil.parseRole(VALID_ROLE));
        assertEquals(Role.STUDENT, ParserUtil.parseRole(" STUDENT "));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    /**
     * Checks that address values without a content character produce actionable parser feedback.
     */
    @Test
    public void parseAddress_noContentCharacter_throwsParseException() {
        for (String address : new String[]{"\u200B", "\u00A0\u200D", "\u0301\uFE0F"}) {
            assertThrows(ParseException.class, Address.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseAddress(address));
        }
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
