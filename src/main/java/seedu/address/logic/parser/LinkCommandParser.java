package seedu.address.logic.parser;

import java.util.List;

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
        List<Index> indices = ParserUtil.parseTwoDisplayedIndices(args, LinkCommand.MESSAGE_INVALID_FORMAT);
        return new LinkCommand(indices.get(0), indices.get(1));
    }
}
