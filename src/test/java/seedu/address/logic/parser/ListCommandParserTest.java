package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests argument validation for the list command.
 */
public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_whitespaceOnly_returnsListCommand() throws ParseException {
        assertTrue(parser.parse("") instanceof ListCommand);
        assertTrue(parser.parse("   ") instanceof ListCommand);
        assertTrue(parser.parse("\t") instanceof ListCommand);
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        String expectedMessage = "Invalid command format. Expected: list";

        assertParseFailure(parser, " 3", expectedMessage);
        assertParseFailure(parser, " extra text", expectedMessage);
        assertParseFailure(parser, " r/student", expectedMessage);
    }
}
