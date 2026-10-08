package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.model.Model;

/**
 * Displays all contacts in insertion order and reports their count.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Showing %1$d contacts.";
    public static final String MESSAGE_EMPTY_LIST = "No contacts to display.";
    public static final String MESSAGE_INVALID_ARGUMENTS =
            "Invalid command format. Expected: " + COMMAND_WORD;

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        int contactCount = model.getFilteredPersonList().size();
        String feedback = contactCount == 0
                ? MESSAGE_EMPTY_LIST
                : String.format(MESSAGE_SUCCESS, contactCount);

        return new CommandResult(feedback);
    }
}
