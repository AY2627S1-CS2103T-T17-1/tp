package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.ContactMatchesCriteriaPredicate;
import seedu.address.model.person.PersonNameComparator;

/**
 * Displays contacts satisfying all supplied criteria, using name order unless another ordering is active.
 */
public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " [n/KEYWORDS] [s/SUBJECT] [l/LEVEL]";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Expected: " + MESSAGE_USAGE;
    public static final String MESSAGE_EMPTY_NAME = "Name filter requires at least one keyword.";
    public static final String MESSAGE_SUCCESS = "Showing %1$d matching contacts.";
    public static final String MESSAGE_NO_MATCHES = "No matching contacts.";

    private final ContactMatchesCriteriaPredicate predicate;

    /**
     * Creates a filter command with the supplied validated criteria.
     *
     * @param predicate Criteria that every displayed contact must satisfy.
     * @throws NullPointerException If {@code predicate} is {@code null}.
     */
    public FilterCommand(ContactMatchesCriteriaPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    /**
     * Replaces the displayed filter and applies name ordering when no comparator is active.
     * Leaves stored contacts unchanged.
     *
     * @param model Model whose displayed view is updated.
     * @return Feedback containing the match count, or a no-matches message.
     * @throws NullPointerException If {@code model} is {@code null}.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        if (model.getPersonListComparator() == null) {
            model.setPersonListComparator(new PersonNameComparator());
        }
        int count = model.getFilteredPersonList().size();
        return new CommandResult(count == 0 ? MESSAGE_NO_MATCHES : String.format(MESSAGE_SUCCESS, count));
    }

    @Override
    public boolean isDataChanging() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FilterCommand otherCommand && predicate.equals(otherCommand.predicate);
    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
