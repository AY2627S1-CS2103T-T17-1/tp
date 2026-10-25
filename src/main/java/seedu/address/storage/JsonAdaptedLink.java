package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Phone;

/**
 * Jackson-friendly version of {@link ParentStudentLink}.
 * Each side of the link is saved as the contact's name and phone number so that users can read and edit it by hand.
 */
class JsonAdaptedLink {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Link's %s field is missing!";

    private final String parentName;
    private final String parentPhone;
    private final String studentName;
    private final String studentPhone;

    /**
     * Constructs a {@code JsonAdaptedLink} with the given link details.
     */
    @JsonCreator
    public JsonAdaptedLink(@JsonProperty("parentName") String parentName,
            @JsonProperty("parentPhone") String parentPhone,
            @JsonProperty("studentName") String studentName,
            @JsonProperty("studentPhone") String studentPhone) {
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.studentName = studentName;
        this.studentPhone = studentPhone;
    }

    /**
     * Converts a given {@code ParentStudentLink} into this class for Jackson use.
     */
    public JsonAdaptedLink(ParentStudentLink source) {
        parentName = source.getParentName().fullName;
        parentPhone = source.getParentPhone().value;
        studentName = source.getStudentName().fullName;
        studentPhone = source.getStudentPhone().value;
    }

    /**
     * Converts this Jackson-friendly adapted link object into the model's {@code ParentStudentLink} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted link.
     */
    public ParentStudentLink toModelType() throws IllegalValueException {
        return new ParentStudentLink(toName(parentName, "parentName"), toPhone(parentPhone, "parentPhone"),
                toName(studentName, "studentName"), toPhone(studentPhone, "studentPhone"));
    }

    private static Name toName(String name, String fieldName) throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, fieldName));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(name);
    }

    private static Phone toPhone(String phone, String fieldName) throws IllegalValueException {
        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, fieldName));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(phone);
    }
}
