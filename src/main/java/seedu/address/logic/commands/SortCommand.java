package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonLevelComparator;

/**
 * Sorts displayed contacts by academic level without changing saved contact data.
 */
public class SortCommand extends Command {
    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " level";
    public static final String MESSAGE_SUCCESS = "Contacts sorted by academic level.";
    public static final String MESSAGE_INVALID_ARGUMENTS =
            "Invalid command format. Expected: " + MESSAGE_USAGE;

    private static final Comparator<Person> LEVEL_COMPARATOR = new PersonLevelComparator();

    /**
     * Applies academic-level ordering while retaining the current filter.
     *
     * @param model Model containing the displayed contacts.
     * @return Feedback confirming that sorting has been applied.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setPersonListComparator(LEVEL_COMPARATOR);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * Returns false because sorting changes presentation rather than contact data.
     *
     * @return False, as sorting does not modify contact data.
     */
    @Override
    public boolean isDataChanging() {
        return false;
    }
}
