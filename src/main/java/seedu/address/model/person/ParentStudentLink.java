package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a link from a parent contact to a student contact.
 * Each side is identified by its name and phone number, compared in the same normalized way as the duplicate-contact
 * rules, so the link stays valid when unrelated details such as the address or note change.
 * Guarantees: immutable; all details are present and not null.
 */
public class ParentStudentLink {

    public static final String MESSAGE_CONSTRAINTS = "A link must connect a parent contact to a student contact.";

    private final Name parentName;
    private final Phone parentPhone;
    private final Name studentName;
    private final Phone studentPhone;

    /**
     * Creates a link from the parent identified by {@code parentName} and {@code parentPhone}
     * to the student identified by {@code studentName} and {@code studentPhone}.
     */
    public ParentStudentLink(Name parentName, Phone parentPhone, Name studentName, Phone studentPhone) {
        requireAllNonNull(parentName, parentPhone, studentName, studentPhone);
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.studentName = studentName;
        this.studentPhone = studentPhone;
    }

    /**
     * Returns a link from {@code parent} to {@code student}.
     *
     * @throws IllegalArgumentException If {@code parent} is not a parent or {@code student} is not a student.
     */
    public static ParentStudentLink between(Person parent, Person student) {
        requireAllNonNull(parent, student);
        checkArgument(parent.getRole() == Role.PARENT && student.getRole() == Role.STUDENT, MESSAGE_CONSTRAINTS);
        return new ParentStudentLink(parent.getName(), parent.getPhone(), student.getName(), student.getPhone());
    }

    public Name getParentName() {
        return parentName;
    }

    public Phone getParentPhone() {
        return parentPhone;
    }

    public Name getStudentName() {
        return studentName;
    }

    public Phone getStudentPhone() {
        return studentPhone;
    }

    /**
     * Returns true if {@code person} is the parent side of this link.
     */
    public boolean isParent(Person person) {
        return person.getRole() == Role.PARENT && isIdentifiedBy(person, parentName, parentPhone);
    }

    /**
     * Returns true if {@code person} is the student side of this link.
     */
    public boolean isStudent(Person person) {
        return person.getRole() == Role.STUDENT && isIdentifiedBy(person, studentName, studentPhone);
    }

    /**
     * Returns true if {@code person} is either side of this link.
     */
    public boolean involves(Person person) {
        return isParent(person) || isStudent(person);
    }

    /**
     * Returns a copy of this link that identifies {@code editedPerson} wherever it identified {@code target}.
     * Returns this link unchanged if it does not involve {@code target}.
     */
    public ParentStudentLink withContactReplaced(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        assert target.getRole() == editedPerson.getRole() : "Editing a contact must not change its role";

        if (isParent(target)) {
            return new ParentStudentLink(editedPerson.getName(), editedPerson.getPhone(), studentName, studentPhone);
        }
        if (isStudent(target)) {
            return new ParentStudentLink(parentName, parentPhone, editedPerson.getName(), editedPerson.getPhone());
        }
        return this;
    }

    private static boolean isIdentifiedBy(Person person, Name name, Phone phone) {
        return person.getName().isSameName(name) && person.getPhone().isSamePhone(phone);
    }

    /**
     * Returns the phone number's digits, which is how {@link Phone#isSamePhone(Phone)} compares phone numbers.
     */
    private static String digitsOf(Phone phone) {
        return phone.value.replaceAll("\\D", "");
    }

    /**
     * Returns true if both links connect the same parent and student, using normalized name and phone matching.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ParentStudentLink otherLink)) {
            return false;
        }

        return parentName.isSameName(otherLink.parentName)
                && parentPhone.isSamePhone(otherLink.parentPhone)
                && studentName.isSameName(otherLink.studentName)
                && studentPhone.isSamePhone(otherLink.studentPhone);
    }

    @Override
    public int hashCode() {
        // Hashes only the phone digits so that links which are equal under normalized matching share a hash.
        return Objects.hash(digitsOf(parentPhone), digitsOf(studentPhone));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("parentName", parentName)
                .add("parentPhone", parentPhone)
                .add("studentName", studentName)
                .add("studentPhone", studentPhone)
                .toString();
    }
}
