package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;

/**
 * Tests the parsing behavior of {@link RemarkCommandParser}.
 */
public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validInput_success() {
        assertParseSuccess(parser, "1 r/Likes baseball", new RemarkCommand(INDEX_FIRST_PERSON, "Likes baseball"));
    }

    @Test
    public void parse_emptyRemark_success() {
        assertParseSuccess(parser, "1 r/", new RemarkCommand(INDEX_FIRST_PERSON, ""));
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0 r/remark", String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, "1 extra r/remark", String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }
}
