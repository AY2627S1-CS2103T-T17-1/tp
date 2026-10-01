package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents an optional remark for a person in the address book.
 * Guarantees: immutable.
 */
public class Remark {

    public final String value;

    /**
     * Constructs a {@code Remark} with the given value.
     *
     * @param remark A non-null remark value.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
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

        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
