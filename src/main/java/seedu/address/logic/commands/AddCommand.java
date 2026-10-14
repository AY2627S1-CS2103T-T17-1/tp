package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE =
            "add r/ROLE n/NAME p/PHONE e/EMAIL a/ADDRESS [s/SUBJECT] [l/LEVEL] [o/NOTE]";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Expected: " + MESSAGE_USAGE;
    public static final String MESSAGE_MISSING_STUDENT_FIELDS =
            "Student contacts require both s/SUBJECT and l/LEVEL.";
    public static final String MESSAGE_PARENT_FIELDS = "Subject and level are only valid for student contacts.";

    public static final String MESSAGE_SUCCESS = "Added %1$s: %2$s.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This contact duplicates an existing %1$s: %2$s.";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PERSON, toAdd.getRole(), toAdd.getName()));
        }

        model.addPerson(toAdd);
        return CommandResult.selectContact(
                String.format(MESSAGE_SUCCESS, toAdd.getRole(), toAdd.getName().fullName), toAdd);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
