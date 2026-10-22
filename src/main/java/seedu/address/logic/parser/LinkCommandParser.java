package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;

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

        return new LinkCommand(parseTargetIndex(arguments[0]), parseTargetIndex(arguments[1]));
    }

    /**
     * Parses a positive decimal index without allowing integer overflow.
     * This mirrors the index rules of {@code DeleteCommandParser} so both commands accept the same indexes.
     *
     * @param indexValue Decimal index supplied by the user.
     * @return An index representable by the application's index type.
     * @throws ParseException If the value is invalid or exceeds the index range.
     */
    private Index parseTargetIndex(String indexValue) throws ParseException {
        if (!indexValue.matches("[0-9]+")) {
            throw new ParseException(LinkCommand.MESSAGE_INVALID_INDEX);
        }

        BigInteger numericIndex = new BigInteger(indexValue);
        if (numericIndex.signum() == 0) {
            throw new ParseException(LinkCommand.MESSAGE_INVALID_INDEX);
        }

        // An index too large for an int cannot exist in the displayed list.
        if (numericIndex.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new ParseException(String.format(LinkCommand.MESSAGE_INDEX_OUT_OF_RANGE, numericIndex));
        }

        return Index.fromOneBased(numericIndex.intValueExact());
    }
}
