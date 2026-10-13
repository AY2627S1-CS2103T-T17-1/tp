package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

/**
 * Represents a contact's role in TutorContacts.
 */
public enum Role {
    STUDENT("student"),
    PARENT("parent");

    public static final String MESSAGE_CONSTRAINTS = "Role must be either student or parent.";

    private final String value;

    Role(String value) {
        this.value = value;
    }

    /**
     * Returns whether {@code test} identifies a supported role, ignoring case.
     */
    public static boolean isValidRole(String test) {
        return test != null && Arrays.stream(values())
                .anyMatch(role -> role.value.equalsIgnoreCase(test));
    }

    /**
     * Returns the supported role identified by {@code value}, ignoring case.
     *
     * @throws IllegalArgumentException If {@code value} does not identify a supported role.
     */
    public static Role fromString(String value) {
        requireNonNull(value);
        return Arrays.stream(values())
                .filter(role -> role.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MESSAGE_CONSTRAINTS));
    }

    /**
     * Returns the canonical lowercase role value.
     */
    @Override
    public String toString() {
        return value;
    }
}
