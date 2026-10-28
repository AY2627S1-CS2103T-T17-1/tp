package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for a parameterless list command.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Returns a list command if the arguments contain only Java whitespace.
     * Uses the whitespace definition of {@link Character#isWhitespace(int)}.
     *
     * @param args Arguments following the command word.
     * @return A command that displays all contacts.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If additional arguments are supplied.
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (!args.isBlank()) {
            throw new ParseException(ListCommand.MESSAGE_INVALID_ARGUMENTS);
        }

        return new ListCommand();
    }
}
