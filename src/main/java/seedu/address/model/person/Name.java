package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Name must be 1-70 characters, start with a letter or digit, and contain only letters, digits, spaces, "
                    + "apostrophes, full stops, or hyphens.";

    // The normalized name must begin with a letter or digit; supported punctuation may follow it.
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N}][\\p{L}\\p{N} .'-]{0,69}";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = normalize(name);
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        return normalize(test).matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if both names have the same normalized value, ignoring capitalization.
     */
    public boolean isSameName(Name otherName) {
        requireNonNull(otherName);
        return fullName.equalsIgnoreCase(otherName.fullName);
    }

    private static String normalize(String name) {
        return name.trim().replaceAll(" +", " ");
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
