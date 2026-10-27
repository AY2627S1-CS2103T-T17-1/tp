package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the single supported sorting criterion.
 */
public class SortCommandParser implements Parser<SortCommand> {
    /**
     * Parses the level criterion, ignoring case and surrounding Java whitespace.
     * Uses the whitespace definition of {@link Character#isWhitespace(int)}.
     *
     * @param args Arguments following the sort command word.
     * @return A command that sorts contacts by academic level.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If the criterion is missing, unsupported, or followed by extra arguments.
     */
    @Override
    public SortCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.strip().equalsIgnoreCase("level")) {
            throw new ParseException(SortCommand.MESSAGE_INVALID_ARGUMENTS);
        }
        return new SortCommand();
    }
}
