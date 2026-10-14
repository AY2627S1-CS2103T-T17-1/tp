package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a parameterless exit command without silently accepting extra arguments.
 */
public class ExitCommandParser implements Parser<ExitCommand> {
    /**
     * Returns an exit command when no arguments are supplied.
     *
     * @param args Arguments following the command word.
     * @return A command requesting safe application exit.
     * @throws ParseException If the input includes additional arguments.
     */
    @Override
    public ExitCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.trim().isEmpty()) {
            throw new ParseException(ExitCommand.MESSAGE_INVALID_ARGUMENTS);
        }
        return new ExitCommand();
    }
}
