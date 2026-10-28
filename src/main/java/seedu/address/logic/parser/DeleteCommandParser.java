package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the single displayed-list index required by a delete command.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses arguments into a command that deletes one contact.
     *
     * @param args Arguments following the command word.
     * @return A command targeting the supplied displayed-list index.
     * @throws ParseException If the syntax or index is invalid.
     */
    @Override
    public DeleteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        String[] arguments = trimmedArgs.split("\\s+");

        if (trimmedArgs.isEmpty() || arguments.length != 1) {
            throw new ParseException(DeleteCommand.MESSAGE_INVALID_FORMAT);
        }

        return new DeleteCommand(ParserUtil.parseDisplayedIndex(arguments[0]));
    }
}
