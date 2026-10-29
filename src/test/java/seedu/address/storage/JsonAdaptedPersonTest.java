package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.INVALID_PARENT_FIELDS_MESSAGE;
import static seedu.address.storage.JsonAdaptedPerson.MESSAGE_NULL_TAG;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Subject;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests JSON-to-model field validation and compatibility with stored contact records.
 */
public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65-";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_ROLE = "teacher";
    private static final String INVALID_SUBJECT = "Music: Theory";
    private static final String INVALID_LEVEL = "Secondary 3/4";
    private static final String INVALID_NOTE = "First line\nSecond line";

    private static final String VALID_ROLE = BENSON.getRole().toString();
    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_SUBJECT = BENSON.getSubject().orElseThrow().toString();
    private static final String VALID_LEVEL = BENSON.getLevel().orElseThrow().toString();
    private static final String VALID_NOTE = "Prefers weekend lessons";
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_savedSupplementaryLetterName_loads() throws Exception {
        // 36 letters (72 UTF-16 units) were valid before names were counted by code point, so saved files must load.
        Person person = new PersonBuilder(BENSON).withName("\uD840\uDC00".repeat(36)).build();
        assertEquals(person, new JsonAdaptedPerson(person).toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, null, VALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, null,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_nullTag_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(null);
        JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, invalidTags);

        assertThrows(IllegalValueException.class, MESSAGE_NULL_TAG, person::toModelType);
    }

    @Test
    public void toModelType_invalidRole_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                createPerson(INVALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        assertThrows(IllegalValueException.class, Role.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullRole_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(null, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Role.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidSubject_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                INVALID_SUBJECT, VALID_LEVEL, VALID_NOTE, VALID_TAGS);
        assertThrows(IllegalValueException.class, Subject.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidLevel_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_SUBJECT, INVALID_LEVEL, VALID_NOTE, VALID_TAGS);
        assertThrows(IllegalValueException.class, Level.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidNote_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_SUBJECT, VALID_LEVEL, INVALID_NOTE, VALID_TAGS);
        assertThrows(IllegalValueException.class, Note.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_parentWithoutStudentFields_returnsParent() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(GEORGE);
        assertEquals(GEORGE, person.toModelType());
    }

    @Test
    public void toModelType_parentWithSubject_throwsIllegalValueException() {
        JsonAdaptedPerson person = createParent(VALID_SUBJECT, null);
        assertThrows(IllegalValueException.class, INVALID_PARENT_FIELDS_MESSAGE, person::toModelType);
    }

    @Test
    public void toModelType_parentWithLevel_throwsIllegalValueException() {
        JsonAdaptedPerson person = createParent(null, VALID_LEVEL);
        assertThrows(IllegalValueException.class, INVALID_PARENT_FIELDS_MESSAGE, person::toModelType);
    }

    @Test
    public void toModelType_parentWithSubjectAndLevel_throwsIllegalValueException() {
        JsonAdaptedPerson person = createParent(VALID_SUBJECT, VALID_LEVEL);
        assertThrows(IllegalValueException.class, INVALID_PARENT_FIELDS_MESSAGE, person::toModelType);
    }

    @Test
    public void toModelType_nullNote_returnsPersonWithoutNote() throws Exception {
        JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_SUBJECT, VALID_LEVEL, null, VALID_TAGS);
        assertEquals(BENSON, person.toModelType());
    }

    /**
     * Checks that previously stored blank notes remain loadable without changing the other contact fields.
     *
     * @throws Exception If a compatible contact cannot be converted.
     */
    @Test
    public void toModelType_whitespaceOnlyNote_returnsPersonWithoutNote() throws Exception {
        for (String blank : List.of("", " \t ", "\u2003", "\u2002\u3000")) {
            JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_SUBJECT, VALID_LEVEL, blank, VALID_TAGS);
            assertEquals(BENSON, person.toModelType());
        }
    }

    /**
     * Checks that nonblank stored notes use the same Unicode padding normalization as commands.
     *
     * @throws Exception If a valid stored note cannot be converted.
     */
    @Test
    public void toModelType_unicodePaddedNote_preservesInternalSpacing() throws Exception {
        JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_SUBJECT, VALID_LEVEL, "\u2003After\u20036 pm\u3000", VALID_TAGS);
        Person expected = new PersonBuilder(BENSON).withNote("After\u20036 pm").build();
        assertEquals(expected, person.toModelType());
        assertEquals(expected, new JsonAdaptedPerson(person.toModelType()).toModelType());
    }

    /**
     * Checks that JSON loading and adapter round trips also handle long email domains safely.
     *
     * @throws Exception If a valid stored email cannot be converted.
     */
    @Test
    public void toModelType_manyEmailDomainLabels_roundTripsValue() throws Exception {
        String email = "a@" + "a.".repeat(2500) + "com";
        JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, email, VALID_ADDRESS,
                VALID_SUBJECT, VALID_LEVEL, null, VALID_TAGS);
        Person expected = new PersonBuilder(BENSON).withEmail(email).build();
        assertEquals(expected, person.toModelType());
        assertEquals(expected, new JsonAdaptedPerson(person.toModelType()).toModelType());
    }

    private static JsonAdaptedPerson createPerson(String role, String name, String phone, String email,
            String address, String subject, String level, String note, List<JsonAdaptedTag> tags) {
        return new JsonAdaptedPerson(role, name, phone, email, address, subject, level, note, tags);
    }

    private static JsonAdaptedPerson createParent(String subject, String level) {
        return createPerson(GEORGE.getRole().toString(), GEORGE.getName().toString(), GEORGE.getPhone().toString(),
                GEORGE.getEmail().toString(), GEORGE.getAddress().toString(), subject, level,
                GEORGE.getNote().orElseThrow().toString(), VALID_TAGS);
    }


    /**
     * Checks that stored levels follow the same supported-year rules as command input.
     */
    @Test
    public void toModelType_unsupportedYear_throwsIllegalValueException() {
        for (String invalid : List.of("JC 5", "Secondary 6", "Adult")) {
            JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_SUBJECT, invalid, VALID_NOTE, VALID_TAGS);
            assertThrows(IllegalValueException.class, Level.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    /**
     * Checks that loading canonicalizes a level and preserves it through adapter conversion.
     */
    @Test
    public void toModelType_lowercaseLevel_roundTripsCanonicalLevel() throws Exception {
        JsonAdaptedPerson person = createPerson(VALID_ROLE, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_SUBJECT, "  jc   2  ", VALID_NOTE, VALID_TAGS);
        Person restored = person.toModelType();
        assertEquals("JC 2", restored.getLevel().orElseThrow().value);
        assertEquals(restored, new JsonAdaptedPerson(restored).toModelType());
    }
}
