package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests the single supported sort criterion and command registration.
 */
public class SortCommandParserTest {
    private final SortCommandParser parser = new SortCommandParser();

    /**
     * Checks accepted argument variants and command registration.
     */
    @Test
    public void parse_levelVariants_returnsSortCommand() throws ParseException {
        for (String valid : List.of("level", " LEVEL ", "  LeVeL  ")) {
            assertInstanceOf(SortCommand.class, parser.parse(valid));
        }
        assertInstanceOf(SortCommand.class, new AddressBookParser().parseCommand(" SORT   LEVEL "));
    }

    /**
     * Checks that unsupported criteria and additional arguments report the syntax error.
     */
    @Test
    public void parse_invalidCriteria_throwsParseException() {
        for (String invalid : List.of("", " ", "name", "subject", "level desc", "level level", "l/level")) {
            assertThrows(ParseException.class, SortCommand.MESSAGE_INVALID_ARGUMENTS, () -> parser.parse(invalid));
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
