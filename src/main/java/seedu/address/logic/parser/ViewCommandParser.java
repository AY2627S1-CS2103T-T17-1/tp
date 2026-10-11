package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;

import seedu.address.commons.core.index.Index;
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

        return new ViewCommand(parseTargetIndex(arguments[0]));
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
            throw new ParseException(ViewCommand.MESSAGE_INVALID_INDEX);
        }

        BigInteger numericIndex = new BigInteger(indexValue);
        if (numericIndex.signum() == 0) {
            throw new ParseException(ViewCommand.MESSAGE_INVALID_INDEX);
        }

        // An index too large for an int cannot exist in the displayed list.
        if (numericIndex.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new ParseException(String.format(ViewCommand.MESSAGE_INDEX_OUT_OF_RANGE, numericIndex));
        }

        return Index.fromOneBased(numericIndex.intValueExact());
    }
}
