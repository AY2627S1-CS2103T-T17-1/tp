package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests acceptance of parameterless exit and rejection of additional arguments.
 */
public class ExitCommandParserTest {
    private final ExitCommandParser parser = new ExitCommandParser();

    @Test
    public void parse_noArguments_returnsExitCommand() throws ParseException {
        assertTrue(parser.parse("") instanceof ExitCommand);
        assertTrue(parser.parse(" \t ") instanceof ExitCommand);
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        assertThrows(ParseException.class, ExitCommand.MESSAGE_INVALID_ARGUMENTS, () -> parser.parse("3"));
        assertThrows(ParseException.class, ExitCommand.MESSAGE_INVALID_ARGUMENTS, () -> parser.parse("x/foo"));
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
