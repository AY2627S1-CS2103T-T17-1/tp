package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;

/**
 * Tests parsing of remark text, empty remarks, and invalid displayed indexes.
 */
public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_returnsRemarkCommand() {
        assertParseSuccess(parser, "1 r/Likes swimming",
                new RemarkCommand(INDEX_FIRST_PERSON, "Likes swimming"));
        assertParseSuccess(parser, "  1  r/  Likes swimming  ",
                new RemarkCommand(INDEX_FIRST_PERSON, "Likes swimming"));
        assertParseSuccess(parser, "1 r/泳ぐのが好き!",
                new RemarkCommand(INDEX_FIRST_PERSON, "泳ぐのが好き!"));
    }

    @Test
    public void parse_emptyOrMissingRemark_returnsEmptyRemark() {
        RemarkCommand expected = new RemarkCommand(INDEX_FIRST_PERSON, "");
        assertParseSuccess(parser, "1 r/", expected);
        assertParseSuccess(parser, "1 r/   ", expected);
        assertParseSuccess(parser, "1", expected);
    }

    @Test
    public void parse_repeatedRemark_usesLastValue() {
        assertParseSuccess(parser, "1 r/old r/new", new RemarkCommand(INDEX_FIRST_PERSON, "new"));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        for (String input : new String[] {"", "r/text", "0 r/text", "-1 r/text", "1.5 r/text",
            "abc r/text", "2147483648 r/text", "1 2 r/text", "1 text"}) {
            assertParseFailure(parser, input, expected);
        }
    }
}
