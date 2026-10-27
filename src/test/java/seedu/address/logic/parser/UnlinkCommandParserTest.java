package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnlinkCommand;

/**
 * Tests that unlink commands are parsed with the shared two-index rules, which ParserUtilTest covers in full.
 */
public class UnlinkCommandParserTest {

    private final UnlinkCommandParser parser = new UnlinkCommandParser();

    @Test
    public void parse_validArgs_returnsUnlinkCommand() {
        assertParseSuccess(parser, " 2  1 ", new UnlinkCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_wrongArgumentCount_throwsParseException() {
        String expectedMessage = "Invalid command format. Expected: unlink PARENT_INDEX STUDENT_INDEX";

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "1 2 3", expectedMessage);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "1 0", "Index must be a positive integer shown in the current list.");
    }
}
