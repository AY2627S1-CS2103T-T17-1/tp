package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an optional note about a contact.
 * Guarantees: immutable; is valid as declared in {@link #isValidNote(String)}.
 */
public class Note {

    public static final String MESSAGE_CONSTRAINTS =
            "Note must contain 1-200 printable characters on one line.";
    public static final String VALIDATION_REGEX = "[^\\p{Cc}\\p{Zl}\\p{Zp}]{1,200}";

    public final String value;

    /**
     * Constructs a {@code Note} after removing surrounding Java whitespace.
     *
     * @param note A valid nonblank note, optionally padded with Java whitespace.
     */
    public Note(String note) {
        requireNonNull(note);
        checkArgument(isValidNote(note), MESSAGE_CONSTRAINTS);
        value = normalize(note);
    }

    /**
     * Returns true if the note is valid after removing surrounding Java whitespace.
     */
    public static boolean isValidNote(String test) {
        return test != null && normalize(test).matches(VALIDATION_REGEX);
    }

    /**
     * Returns note text without surrounding Java whitespace, preserving internal spacing.
     *
     * @param note Non-null note text.
     * @return The normalized note.
     */
    private static String normalize(String note) {
        return note.strip();
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

        if (!(other instanceof Note otherNote)) {
            return false;
        }

        return value.equals(otherNote.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
