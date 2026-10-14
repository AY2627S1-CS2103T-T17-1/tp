package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests parameter boundaries, canonical prefix recognition, and literal-prefix escapes.
 */
public class ArgumentTokenizerTest {

    private final Prefix unknownPrefix = new Prefix("--u");
    private final Prefix pSlash = new Prefix("p/");
    private final Prefix dashT = new Prefix("-t");
    private final Prefix hatQ = new Prefix("^Q");

    @Test
    public void tokenize_emptyArgsString_noValues() {
        String argsString = "  ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);

        assertPreambleEmpty(argMultimap);
        assertArgumentAbsent(argMultimap, pSlash);
    }

    private void assertPreamblePresent(ArgumentMultimap argMultimap, String expectedPreamble) {
        assertEquals(expectedPreamble, argMultimap.getPreamble());
    }

    private void assertPreambleEmpty(ArgumentMultimap argMultimap) {
        assertTrue(argMultimap.getPreamble().isEmpty());
    }

    /**
     * Asserts all the arguments in {@code argMultimap} with {@code prefix} match the {@code expectedValues}
     * and only the last value is returned upon calling {@code ArgumentMultimap#getValue(Prefix)}.
     */
    private void assertArgumentPresent(ArgumentMultimap argMultimap, Prefix prefix, String... expectedValues) {

        // Verify the last value is returned
        assertEquals(expectedValues[expectedValues.length - 1], argMultimap.getValue(prefix).get());

        // Verify the number of values returned is as expected
        assertEquals(expectedValues.length, argMultimap.getAllValues(prefix).size());

        // Verify all values returned are as expected and in order
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], argMultimap.getAllValues(prefix).get(i));
        }
    }

    private void assertArgumentAbsent(ArgumentMultimap argMultimap, Prefix prefix) {
        assertFalse(argMultimap.getValue(prefix).isPresent());
    }

    @Test
    public void tokenize_noPrefixes_allTakenAsPreamble() {
        String argsString = "  some random string /t tag with leading and trailing spaces ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString);

        // Same string expected as preamble, but leading/trailing spaces should be trimmed
        assertPreamblePresent(argMultimap, argsString.trim());

    }

    @Test
    public void tokenize_oneArgument() {
        // Preamble present
        String argsString = "  Some preamble string p/ Argument value ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);
        assertPreamblePresent(argMultimap, "Some preamble string");
        assertArgumentPresent(argMultimap, pSlash, "Argument value");

        // No preamble
        argsString = " p/   Argument value ";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);
        assertPreambleEmpty(argMultimap);
        assertArgumentPresent(argMultimap, pSlash, "Argument value");

    }

    @Test
    public void tokenize_multipleArguments() {
        // Only two arguments are present
        String argsString = "SomePreambleString -t dashT-Value p/pSlash value";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value");
        assertArgumentAbsent(argMultimap, hatQ);

        // All three arguments are present
        argsString = "Different Preamble String ^Q111 -t dashT-Value p/pSlash value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "Different Preamble String");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value");
        assertArgumentPresent(argMultimap, hatQ, "111");

        /* Also covers: Reusing of the tokenizer multiple times */

        // Reuse tokenizer on an empty string to ensure ArgumentMultimap is correctly reset
        // (i.e. no stale values from the previous tokenizing remain)
        argsString = "";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreambleEmpty(argMultimap);
        assertArgumentAbsent(argMultimap, pSlash);

        /* Also covers: testing for prefixes not specified as a prefix */

        // Prefixes not previously given to the tokenizer should not return any values
        argsString = unknownPrefix + "some value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertArgumentAbsent(argMultimap, unknownPrefix);
        assertPreamblePresent(argMultimap, argsString); // Unknown prefix is taken as part of preamble
    }

    @Test
    public void tokenize_multipleArgumentsWithRepeats() {
        // Two arguments repeated, some have empty values
        String argsString = "SomePreambleString -t dashT-Value ^Q ^Q -t another dashT value p/ pSlash value -t";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value", "another dashT value", "");
        assertArgumentPresent(argMultimap, hatQ, "", "");
    }

    @Test
    public void tokenize_multipleArgumentsJoined() {
        String argsString = "SomePreambleStringp/ pSlash joined-tjoined -t not joined^Qjoined";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleStringp/ pSlash joined-tjoined");
        assertArgumentAbsent(argMultimap, pSlash);
        assertArgumentPresent(argMultimap, dashT, "not joined^Qjoined");
        assertArgumentAbsent(argMultimap, hatQ);
    }

    @Test
    public void tokenize_mixedCasePrefixesAndTabs_preservesValues() throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeForCommand(
                "N/Alex Tan\tP/91234567", PREFIX_NAME, PREFIX_PHONE);
        assertPreambleEmpty(arguments);
        assertArgumentPresent(arguments, PREFIX_NAME, "Alex Tan");
        assertArgumentPresent(arguments, PREFIX_PHONE, "91234567");
    }

    @Test
    public void tokenize_mixedCaseRepeatedPrefix_rejectsDuplicate() throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeForCommand("n/Alex N/Bob", PREFIX_NAME);
        assertArgumentPresent(arguments, PREFIX_NAME, "Alex", "Bob");
        assertThrows(ParseException.class, "Prefix n/ may be specified only once.", () -> {
            arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME);
        });
    }

    @Test
    public void tokenize_unknownPrefixes_rejectsFirstUnknown() {
        assertThrows(ParseException.class, "Unknown prefix: x/.", () -> {
            ArgumentTokenizer.tokenizeForCommand("X/foo n/Alex", PREFIX_NAME);
        });
        assertThrows(ParseException.class, "Unknown prefix: x/.", () -> {
            ArgumentTokenizer.tokenizeForCommand("o/Discuss x/foo", PREFIX_NOTE);
        });
        assertThrows(ParseException.class, "Unknown prefix: x/.", () -> {
            ArgumentTokenizer.tokenizeForCommand("n/Alex\tX/foo y/bar", PREFIX_NAME);
        });
    }

    @Test
    public void tokenize_escapedKnownAndUnknownPrefixes_keepsLiteralText() throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeForCommand(
                "o/Discuss \\x/foo and \\N/bar", PREFIX_NOTE, PREFIX_NAME);
        assertArgumentPresent(arguments, PREFIX_NOTE, "Discuss x/foo and N/bar");
        assertArgumentAbsent(arguments, PREFIX_NAME);
    }

    @Test
    public void tokenize_escapedValueStartAndBackslashes_preservesLiteralBackslashes() throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeForCommand("a/\\x/foo", PREFIX_ADDRESS);
        assertArgumentPresent(arguments, PREFIX_ADDRESS, "x/foo");

        arguments = ArgumentTokenizer.tokenizeForCommand("a/\\\\x/foo", PREFIX_ADDRESS);
        assertArgumentPresent(arguments, PREFIX_ADDRESS, "\\x/foo");

        arguments = ArgumentTokenizer.tokenizeForCommand("a/C:\\Tutor\\Files", PREFIX_ADDRESS);
        assertArgumentPresent(arguments, PREFIX_ADDRESS, "C:\\Tutor\\Files");
    }

    @Test
    public void tokenize_slashesWithinValues_doNotStartParameters() throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeForCommand(
                "s/Biology/Chemistry a/https://example.com/x/foo", PREFIX_SUBJECT, PREFIX_ADDRESS);
        assertArgumentPresent(arguments, PREFIX_SUBJECT, "Biology/Chemistry");
        assertArgumentPresent(arguments, PREFIX_ADDRESS, "https://example.com/x/foo");
    }

    @Test
    public void tokenize_multipleDuplicateFields_reportsValidationOrder() throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeForCommand(
                "p/111 P/222 n/Alex N/Bob", PREFIX_PHONE, PREFIX_NAME);
        assertThrows(ParseException.class, "Prefix n/ may be specified only once.", () -> {
            arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE);
        });
    }

    @Test
    public void equalsMethod() {
        Prefix aaa = new Prefix("aaa");

        assertEquals(aaa, aaa);
        assertEquals(aaa, new Prefix("aaa"));

        assertNotEquals(aaa, "aaa");
        assertNotEquals(aaa, new Prefix("aab"));
    }

}
