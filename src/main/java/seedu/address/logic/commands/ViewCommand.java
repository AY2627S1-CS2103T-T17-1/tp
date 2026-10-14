package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Shows the complete details of a contact identified by its index in the currently displayed list.
 * Viewing a contact never changes contact data.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " INDEX";
    public static final String MESSAGE_VIEW_CONTACT_SUCCESS = "Showing contact: %1$s.";
    public static final String MESSAGE_INVALID_FORMAT =
            "Invalid command format. Expected: " + MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_INDEX =
            "Index must be a positive integer shown in the current list.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE =
            "No contact exists at index %1$s in the current list.";

    private final Index targetIndex;

    /**
     * Creates a command to view the contact at the specified displayed index.
     *
     * @param targetIndex Index of the contact in the currently displayed list.
     */
    public ViewCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedContacts = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= displayedContacts.size()) {
            throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, targetIndex.getOneBased()));
        }

        Person contactToView = displayedContacts.get(targetIndex.getZeroBased());
        return CommandResult.expandContact(
                String.format(MESSAGE_VIEW_CONTACT_SUCCESS, contactToView.getName().fullName), contactToView);
    }

    /**
     * Returns {@code false} because viewing a contact only reads contact data,
     * so the data file does not need to be saved.
     */
    @Override
    public boolean isDataChanging() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return targetIndex.equals(otherViewCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
