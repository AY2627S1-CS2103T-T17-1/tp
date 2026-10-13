package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a contact identified by its index in the currently displayed list.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " INDEX";
    public static final String MESSAGE_DELETE_CONTACT_SUCCESS = "Deleted contact: %1$s.";
    public static final String MESSAGE_INVALID_FORMAT =
            "Invalid command format. Expected: " + MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_INDEX =
            "Index must be a positive integer shown in the current list.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE =
            "No contact exists at index %1$s in the current list.";

    private final Index targetIndex;

    /**
     * Creates a command to delete the contact at the specified displayed index.
     *
     * @param targetIndex Index of the contact in the currently displayed list.
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedContacts = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= displayedContacts.size()) {
            throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, targetIndex.getOneBased()));
        }

        Person contactToDelete = displayedContacts.get(targetIndex.getZeroBased());
        model.deletePerson(contactToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_CONTACT_SUCCESS, contactToDelete.getName().fullName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
