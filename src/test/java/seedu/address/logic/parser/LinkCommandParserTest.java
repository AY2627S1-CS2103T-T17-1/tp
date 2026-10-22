package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.LinkCommand;

/**
 * Tests syntax and index validation for link commands.
 */
public class LinkCommandParserTest {

    private final LinkCommandParser parser = new LinkCommandParser();

    @Test
    public void parse_validArgs_returnsLinkCommand() {
        LinkCommand expectedCommand = new LinkCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);

        assertParseSuccess(parser, "1 2", expectedCommand);
        assertParseSuccess(parser, "  1   2  ", expectedCommand);
        assertParseSuccess(parser, "\t01\t2", expectedCommand);
    }

    @Test
    public void parse_sameIndexTwice_returnsLinkCommand() {
        // The command, not the parser, rejects linking a contact to itself.
        assertParseSuccess(parser, "1 1", new LinkCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_missingOrExtraArguments_throwsParseException() {
        String expectedMessage = "Invalid command format. Expected: link PARENT_INDEX STUDENT_INDEX";

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "   ", expectedMessage);
        assertParseFailure(parser, "1", expectedMessage);
        assertParseFailure(parser, "1 2 3", expectedMessage);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = "Index must be a positive integer shown in the current list.";

        assertParseFailure(parser, "0 1", expectedMessage);
        assertParseFailure(parser, "1 0", expectedMessage);
        assertParseFailure(parser, "-1 2", expectedMessage);
        assertParseFailure(parser, "1 b", expectedMessage);
    }

    @Test
    public void parse_integerOverflow_throwsParseException() {
        assertParseFailure(parser, "1 2147483648",
                "No contact exists at index 2147483648 in the current list.");
    }
}
