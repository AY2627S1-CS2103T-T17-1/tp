package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the single displayed-list index required by a view command.
 */
public class ViewCommandParser implements Parser<ViewCommand> {

    /**
     * Parses arguments into a command that views one contact.
     *
     * @param args Arguments following the command word.
     * @return A command targeting the supplied displayed-list index.
     * @throws ParseException If the syntax or index is invalid.
     */
    @Override
    public ViewCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        String[] arguments = trimmedArgs.split("\\s+");

        if (trimmedArgs.isEmpty() || arguments.length != 1) {
            throw new ParseException(ViewCommand.MESSAGE_INVALID_FORMAT);
        }

        return new ViewCommand(ParserUtil.parseDisplayedIndex(arguments[0]));
    }
}
