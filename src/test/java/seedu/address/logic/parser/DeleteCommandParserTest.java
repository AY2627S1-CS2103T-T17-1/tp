package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;

/**
 * Tests syntax and index validation for delete commands.
 */
public class DeleteCommandParserTest {

    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "  1  ", new DeleteCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "\t1\t", new DeleteCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "0001", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_missingOrExtraArguments_throwsParseException() {
        String expectedMessage = "Invalid command format. Expected: delete INDEX";

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "   ", expectedMessage);
        assertParseFailure(parser, "1 2", expectedMessage);
        assertParseFailure(parser, "1 extra", expectedMessage);
        assertParseFailure(parser, "1\textra", expectedMessage);
        assertParseFailure(parser, "1 r/student", expectedMessage);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = "Index must be a positive integer shown in the current list.";

        assertParseFailure(parser, "0", expectedMessage);
        assertParseFailure(parser, "0000", expectedMessage);
        assertParseFailure(parser, "-1", expectedMessage);
        assertParseFailure(parser, "+1", expectedMessage);
        assertParseFailure(parser, "1.5", expectedMessage);
        assertParseFailure(parser, "abc", expectedMessage);
    }

    @Test
    public void parse_maximumInteger_returnsDeleteCommand() {
        assertParseSuccess(parser, "2147483647",
                new DeleteCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_integerOverflow_throwsParseException() {
        assertParseFailure(parser, "2147483648",
                "No contact exists at index 2147483648 in the current list.");
        assertParseFailure(parser, "99999999999999999999",
                "No contact exists at index 99999999999999999999 in the current list.");
    }
}
