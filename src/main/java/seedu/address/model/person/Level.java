package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.List;

/**
 * Represents a supported academic level in canonical display form.
 * Guarantees: immutable; only predefined academic stages and years are accepted.
 */
public class Level {

    public static final String MESSAGE_CONSTRAINTS =
            "Level must be Primary 1-6, Secondary 1-5, or JC 1-2 (for example, Secondary 3).";

    /**
     * Defines accepted spellings and academic progression in one place.
     */
    private static final List<String> SUPPORTED_LEVELS = List.of(
            "Primary 1", "Primary 2", "Primary 3", "Primary 4", "Primary 5", "Primary 6",
            "Secondary 1", "Secondary 2", "Secondary 3", "Secondary 4", "Secondary 5",
            "JC 1", "JC 2");

    public final String value;

    /**
     * Constructs a supported level, ignoring case and normalizing spaces.
     *
     * @param level A supported academic level.
     * @throws NullPointerException If the input is null.
     * @throws IllegalArgumentException If the input is unsupported.
     */
    public Level(String level) {
        requireNonNull(level);
        int matchedRank = findRank(level);
        checkArgument(matchedRank >= 0, MESSAGE_CONSTRAINTS);
        value = SUPPORTED_LEVELS.get(matchedRank);
    }

    /**
     * Returns whether text identifies a supported level.
     *
     * @param test Text to validate, which may be null.
     * @return True if the text identifies a supported level.
     */
    public static boolean isValidLevel(String test) {
        return test != null && findRank(test) >= 0;
    }

    /**
     * Returns the zero-based academic progression rank, from Primary 1 to JC 2.
     *
     * @return The academic progression rank.
     */
    public int getSortRank() {
        return SUPPORTED_LEVELS.indexOf(value);
    }

    /**
     * Returns the supported level's position, or -1 if no label matches.
     *
     * @param input Non-null level text to match after space normalization.
     * @return The matching academic rank, or -1 if the text is unsupported.
     */
    private static int findRank(String input) {
        String normalized = input.trim().replaceAll(" +", " ");
        for (int index = 0; index < SUPPORTED_LEVELS.size(); index++) {
            if (SUPPORTED_LEVELS.get(index).equalsIgnoreCase(normalized)) {
                return index;
            }
        }
        return -1;
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
