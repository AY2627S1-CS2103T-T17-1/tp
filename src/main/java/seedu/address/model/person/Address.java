package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an immutable, single-line address containing a Unicode letter, number, punctuation mark, or symbol.
 * Preserves formatting and combining marks alongside that content after {@link String#trim()} normalization.
 */
public class Address {

    public static final String MESSAGE_CONSTRAINTS =
            "Address must be 1-150 characters on one line, contain no control characters, "
                    + "and include a letter, number, punctuation mark, or symbol.";

    // Require a content category without forbidding accompanying marks or format characters.
    // Controls and Unicode line separators remain forbidden in the normalized address.
    public static final String VALIDATION_REGEX =
            "(?=.*[\\p{L}\\p{N}\\p{P}\\p{S}])[^\\p{Cc}\\p{Zl}\\p{Zp}]{1,150}";

    public final String value;

    /**
     * Constructs an {@code Address} after removing surrounding characters at or below U+0020.
     *
     * @param address A valid address.
     * @throws IllegalArgumentException If the normalized address violates the length, line, or content constraints.
     * @throws NullPointerException If the input is null.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        value = address.trim();
    }

    /**
     * Returns whether trimming produces a valid address containing a letter, number, punctuation mark, or symbol.
     * Spaces, formatting, and combining marks do not satisfy the content requirement on their own.
     * Unicode categories determine validity independently of how a particular font renders the text.
     *
     * @param test Address text to validate using {@link String#trim()} normalization.
     * @return Whether the normalized value meets all address constraints.
     * @throws NullPointerException If the input is null.
     */
    public static boolean isValidAddress(String test) {
        requireNonNull(test);
        return test.trim().matches(VALIDATION_REGEX);
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
        if (!(other instanceof Address otherAddress)) {
            return false;
        }

        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
