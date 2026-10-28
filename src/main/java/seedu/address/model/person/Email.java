package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    public static final String MESSAGE_CONSTRAINTS =
            "Email must be in the form local-part@domain, for example alex@example.com.";
    private static final String DOMAIN_LABEL_REGEX = "[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?";

    // Labels cannot contain dots, so possessive repetition avoids recursive backtracking across label boundaries.
    public static final String VALIDATION_REGEX = "[^\\s\\p{Z}@\\p{Cc}]+@" + DOMAIN_LABEL_REGEX
            + "(?:\\." + DOMAIN_LABEL_REGEX + ")++";

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email.trim();
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidEmail(String test) {
        requireNonNull(test);
        return test.trim().matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if both email addresses are equal, ignoring case.
     */
    public boolean isSameEmail(Email otherEmail) {
        requireNonNull(otherEmail);
        return value.equalsIgnoreCase(otherEmail.value);
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

        // instanceof handles nulls
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
