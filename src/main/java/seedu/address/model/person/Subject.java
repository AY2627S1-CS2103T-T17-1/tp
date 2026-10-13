package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the subject taught to a student contact.
 * Guarantees: immutable; is valid as declared in {@link #isValidSubject(String)}.
 */
public class Subject {

    public static final String MESSAGE_CONSTRAINTS =
            "Subject must be 1-40 characters and use only letters, digits, spaces, or common subject punctuation.";
    public static final String VALIDATION_REGEX = "[\\p{Alnum}&+\\-'/()., ]{1,40}";

    public final String value;

    /**
     * Constructs a {@code Subject}.
     *
     * @param subject A valid subject.
     */
    public Subject(String subject) {
        requireNonNull(subject);
        String normalizedSubject = normalize(subject);
        checkArgument(isValidSubject(subject), MESSAGE_CONSTRAINTS);
        value = normalizedSubject;
    }

    /**
     * Returns true if the given value is a valid subject.
     */
    public static boolean isValidSubject(String test) {
        return test != null && normalize(test).matches(VALIDATION_REGEX);
    }

    private static String normalize(String subject) {
        return subject.trim().replaceAll(" +", " ");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Subject otherSubject)) {
            return false;
        }

        return value.equals(otherSubject.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
