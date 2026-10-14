package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone must contain 3-15 digits, with an optional leading + and optional spaces or hyphens between digits.";
    public static final String VALIDATION_REGEX = "\\+?[0-9](?:[ -]*[0-9]){2,14}";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone.trim();
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        requireNonNull(test);
        return test.trim().matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if both phone numbers have the same normalized digits.
     */
    public boolean isSamePhone(Phone otherPhone) {
        requireNonNull(otherPhone);
        return normalize(value).equals(normalize(otherPhone.value));
    }

    private static String normalize(String phone) {
        return phone.replaceAll("[+ \\-]", "");
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
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
