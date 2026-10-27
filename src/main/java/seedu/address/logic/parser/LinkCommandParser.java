package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the parent and student displayed-list indices required by a link command.
 */
public class LinkCommandParser implements Parser<LinkCommand> {

    /**
     * Parses arguments into a command that links a parent to a student.
     *
     * @param args Arguments following the command word.
     * @return A command targeting the supplied displayed-list indices.
     * @throws ParseException If the syntax or either index is invalid.
     */
    @Override
    public LinkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        String[] arguments = trimmedArgs.split("\\s+");

        if (trimmedArgs.isEmpty() || arguments.length != 2) {
            throw new ParseException(LinkCommand.MESSAGE_INVALID_FORMAT);
        }

        Index parentIndex = ParserUtil.parseDisplayedIndex(arguments[0]);
        Index studentIndex = ParserUtil.parseDisplayedIndex(arguments[1]);
        return new LinkCommand(parentIndex, studentIndex);
    }
}
