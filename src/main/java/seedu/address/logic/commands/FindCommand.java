package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Finds and lists all contacts whose names contain any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all contacts whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    public static final String MESSAGE_SUCCESS = "Showing %1$d matching contacts.";
    public static final String MESSAGE_NO_MATCHES = "No matching contacts.";

    private final NameContainsKeywordsPredicate predicate;

    /**
     * Creates a find command with the supplied name-matching predicate.
     *
     * @param predicate Criteria used to match contact names.
     */
    public FindCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    /**
     * Displays matching contacts and reports their count, or a no-matches message.
     *
     * @param model Model whose displayed contact list is updated.
     * @return Feedback containing the match count, or a no-matches message.
     * @throws NullPointerException If {@code model} is {@code null}.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int contactCount = model.getFilteredPersonList().size();
        String feedback = contactCount == 0
                ? MESSAGE_NO_MATCHES
                : String.format(MESSAGE_SUCCESS, contactCount);
        return new CommandResult(feedback);
    }

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
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
