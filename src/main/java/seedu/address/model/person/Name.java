package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.text.Normalizer;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Name must be 1-70 characters, start with a letter or digit, and contain only letters (with any accent "
                    + "marks), digits, spaces, straight apostrophes ('), full stops, or hyphens. Slashes are not "
                    + "allowed, so write a name such as Ravi s/o Kumar as Ravi s.o. Kumar.";

    public static final int MAX_LENGTH = 70;

    // The normalized name must begin with a letter or digit; supported punctuation may follow it.
    // Combining marks (\\p{M}), such as accents and vowel signs, may only follow a letter or digit.
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N}]\\p{M}*(?:[\\p{L}\\p{N}]\\p{M}*|[ .'-])*";

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
        String normalized = normalize(test);
        // Count code points, not UTF-16 units, so a letter outside the Basic Multilingual Plane counts once.
        return normalized.codePointCount(0, normalized.length()) <= MAX_LENGTH
                && normalized.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if both names have the same normalized value, ignoring capitalization.
     */
    public boolean isSameName(Name otherName) {
        requireNonNull(otherName);
        return fullName.equalsIgnoreCase(otherName.fullName);
    }

    /**
     * Returns the name in Unicode NFC form with surrounding spaces removed and inner runs of spaces collapsed.
     * NFC makes a decomposed spelling, such as {@code e} followed by a combining acute accent, equal to its
     * precomposed form {@code é}, so both spellings are stored, compared, and searched identically.
     */
    public static String normalize(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFC).trim().replaceAll(" +", " ");
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
