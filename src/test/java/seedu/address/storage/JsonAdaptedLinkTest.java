package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Phone;

/**
 * Tests conversion between saved links and {@code ParentStudentLink}.
 */
public class JsonAdaptedLinkTest {

    private static final String VALID_PARENT_NAME = GEORGE.getName().fullName;
    private static final String VALID_PARENT_PHONE = GEORGE.getPhone().value;
    private static final String VALID_STUDENT_NAME = ALICE.getName().fullName;
    private static final String VALID_STUDENT_PHONE = ALICE.getPhone().value;
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "12a";

    @Test
    public void toModelType_validLink_returnsLink() throws Exception {
        ParentStudentLink link = ParentStudentLink.between(GEORGE, ALICE);

        assertEquals(link, new JsonAdaptedLink(link).toModelType());
    }

    @Test
    public void toModelType_missingParentName_throwsIllegalValueException() {
        JsonAdaptedLink link = new JsonAdaptedLink(null, VALID_PARENT_PHONE, VALID_STUDENT_NAME,
                VALID_STUDENT_PHONE);
        String expectedMessage = String.format(JsonAdaptedLink.MISSING_FIELD_MESSAGE_FORMAT, "parentName");

        assertThrows(IllegalValueException.class, expectedMessage, link::toModelType);
    }

    @Test
    public void toModelType_missingStudentPhone_throwsIllegalValueException() {
        JsonAdaptedLink link = new JsonAdaptedLink(VALID_PARENT_NAME, VALID_PARENT_PHONE, VALID_STUDENT_NAME,
                null);
        String expectedMessage = String.format(JsonAdaptedLink.MISSING_FIELD_MESSAGE_FORMAT, "studentPhone");

        assertThrows(IllegalValueException.class, expectedMessage, link::toModelType);
    }

    @Test
    public void toModelType_invalidStudentName_throwsIllegalValueException() {
        JsonAdaptedLink link = new JsonAdaptedLink(VALID_PARENT_NAME, VALID_PARENT_PHONE, INVALID_NAME,
                VALID_STUDENT_PHONE);

        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, link::toModelType);
    }

    @Test
    public void toModelType_invalidParentPhone_throwsIllegalValueException() {
        JsonAdaptedLink link = new JsonAdaptedLink(VALID_PARENT_NAME, INVALID_PHONE, VALID_STUDENT_NAME,
                VALID_STUDENT_PHONE);

        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, link::toModelType);
    }
}
