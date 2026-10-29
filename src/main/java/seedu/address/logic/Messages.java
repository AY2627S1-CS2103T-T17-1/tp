package seedu.address.logic;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_DISPLAYED_INDEX =
            "Index must be a positive integer shown in the current list.";
    public static final String MESSAGE_DISPLAYED_INDEX_OUT_OF_RANGE =
            "No contact exists at index %1$s in the current list.";
    public static final String MESSAGE_DUPLICATE_FIELDS =
            "Prefix %s may be specified only once.";

    private static final String NOT_RECORDED = "Not recorded";

    /**
     * Returns the error message for the first duplicated prefix in the supplied validation order.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        return String.format(MESSAGE_DUPLICATE_FIELDS, duplicatePrefixes[0]);
    }

    /**
     * Formats the {@code person} for display to the user, starting with its role, name, and phone.
     * Every field that can be edited is included, so the edit confirmation shows the values that were saved.
     * Students show their subject and level, or {@code Not recorded} for older records without them, as the
     * expanded contact card does. Tags appear only if the contact has any.
     *
     * @param person Contact to describe.
     * @return A one-line description such as {@code student: Alex Tan (91234567); Email: ...; Note: None}.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getRole())
                .append(": ")
                .append(person.getName())
                .append(" (")
                .append(person.getPhone())
                .append("); Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress());
        if (person.getRole() == Role.STUDENT) {
            builder.append("; Subject: ").append(person.getSubject().map(Object::toString).orElse(NOT_RECORDED))
                    .append("; Level: ").append(person.getLevel().map(Object::toString).orElse(NOT_RECORDED));
        }
        builder.append("; Note: ").append(person.getNote().map(Object::toString).orElse("None"));
        if (!person.getTags().isEmpty()) {
            builder.append("; Tags: ");
            person.getTags().forEach(builder::append);
        }
        return builder.toString();
    }

}
