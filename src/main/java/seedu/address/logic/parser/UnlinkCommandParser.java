package seedu.address.logic.parser;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnlinkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the parent and student displayed-list indices required by an unlink command.
 */
public class UnlinkCommandParser implements Parser<UnlinkCommand> {

    /**
     * Parses arguments into a command that unlinks a parent from a student.
     *
     * @param args Arguments following the command word.
     * @return A command targeting the supplied displayed-list indices.
     * @throws ParseException If the syntax or either index is invalid.
     */
    @Override
    public UnlinkCommand parse(String args) throws ParseException {
        List<Index> indices = ParserUtil.parseTwoDisplayedIndices(args, UnlinkCommand.MESSAGE_INVALID_FORMAT);
        return new UnlinkCommand(indices.get(0), indices.get(1));
    }
}
