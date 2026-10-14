package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student contact's academic level.
 * Guarantees: immutable; is valid as declared in {@link #isValidLevel(String)}.
 */
public class Level {

    public static final String MESSAGE_CONSTRAINTS =
            "Level must be 1-30 characters and contain only letters, digits, spaces, apostrophes, or hyphens.";
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N} '\\-]{1,30}";

    public final String value;

    /**
     * Constructs a {@code Level}.
     *
     * @param level A valid academic level.
     */
    public Level(String level) {
        requireNonNull(level);
        String normalizedLevel = normalize(level);
        checkArgument(isValidLevel(level), MESSAGE_CONSTRAINTS);
        value = normalizedLevel;
    }

    /**
     * Returns true if the given value is a valid academic level.
     */
    public static boolean isValidLevel(String test) {
        return test != null && normalize(test).matches(VALIDATION_REGEX);
    }

    private static String normalize(String level) {
        return level.trim().replaceAll(" +", " ");
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

        if (!(other instanceof Level otherLevel)) {
            return false;
        }

        return value.equals(otherLevel.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
