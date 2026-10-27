package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewCommand;

/**
 * Tests syntax and index validation for view commands.
 */
public class ViewCommandParserTest {

    private final ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validArgs_returnsViewCommand() {
        assertParseSuccess(parser, "1", new ViewCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "  1  ", new ViewCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "\t1\t", new ViewCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "0001", new ViewCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_missingOrExtraArguments_throwsParseException() {
        String expectedMessage = "Invalid command format. Expected: view INDEX";

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "   ", expectedMessage);
        assertParseFailure(parser, "1 2", expectedMessage);
        assertParseFailure(parser, "1 extra", expectedMessage);
        assertParseFailure(parser, "1\textra", expectedMessage);
        assertParseFailure(parser, "1 r/student", expectedMessage);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        // The full index rules are tested in ParserUtilTest; these cases check that view applies them.
        assertParseFailure(parser, "0", "Index must be a positive integer shown in the current list.");
        assertParseFailure(parser, "abc", "Index must be a positive integer shown in the current list.");
        assertParseFailure(parser, "2147483648", "No contact exists at index 2147483648 in the current list.");
    }
}
