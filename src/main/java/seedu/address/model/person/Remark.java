package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents an immutable, optional remark about a person.
 * Accepts any non-null text, including an empty string to indicate no remark.
 */
public class Remark {

    public final String value;

    /**
     * Constructs a remark with the given text.
     *
     * @param remark Non-null remark text.
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
