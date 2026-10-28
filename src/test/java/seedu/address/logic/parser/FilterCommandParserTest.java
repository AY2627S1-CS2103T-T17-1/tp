package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.ContactMatchesCriteriaPredicate;
import seedu.address.model.person.Level;
import seedu.address.model.person.Subject;

/**
 * Tests filter syntax, field boundaries, normalization, and deterministic validation precedence.
 */
public class FilterCommandParserTest {
    private final FilterCommandParser parser = new FilterCommandParser();

    /**
     * Checks that all seven nonempty subsets of criteria are accepted.
     */
    @Test
    public void parse_validSubsets_acceptsEveryCombination() {
        for (int combination = 1; combination < 8; combination++) {
            boolean hasName = (combination & 1) != 0;
            boolean hasSubject = (combination & 2) != 0;
            boolean hasLevel = (combination & 4) != 0;
            String input = (hasName ? " n/Alice Tan" : "")
                    + (hasSubject ? " s/Mathematics" : "") + (hasLevel ? " l/Secondary 3" : "");
            assertParseSuccess(parser, input, command(hasName ? List.of("Alice", "Tan") : List.of(),
                    hasSubject ? "Mathematics" : null, hasLevel ? "Secondary 3" : null));
        }
    }

    /**
     * Checks command and prefix case handling, prefix order, and whitespace normalization.
     *
     * @throws Exception If parsing valid command text unexpectedly fails.
     */
    @Test
    public void parse_mixedCasePrefixesAndWhitespace_normalizesValues() throws Exception {
        FilterCommand expected = command(List.of("Alice", "TAN"), "Additional Mathematics", "Secondary 3");
        assertParseSuccess(parser, " L/ Secondary   3  N/ Alice \t TAN S/ Additional   Mathematics ", expected);
        assertEquals(expected, new AddressBookParser()
                .parseCommand(" FiLtEr L/Secondary 3 N/Alice TAN S/Additional Mathematics "));
    }

    /**
     * Checks that permitted punctuation and escaped prefix text remain part of criterion values.
     */
    @Test
    public void parse_literalPrefixAndPunctuation_preservesValues() {
        assertParseSuccess(parser, " s/Biology/Chemistry", command(List.of(), "Biology/Chemistry", null));
        assertParseSuccess(parser, " s/Topic \\x/foo", command(List.of(), "Topic x/foo", null));
        assertParseSuccess(parser, " n/Alice-Marie O'Neil", command(List.of("Alice-Marie", "O'Neil"), null, null));
    }

    /**
     * Checks that absent criteria and text preceding the first prefix produce format errors.
     */
    @Test
    public void parse_missingCriteriaOrPreamble_rejectsInput() {
        for (String input : List.of("", " \t ", "Alice", "Alice s/Math", "subject/Math")) {
            assertParseFailure(parser, input, FilterCommand.MESSAGE_INVALID_FORMAT);
        }
    }

    /**
     * Checks that empty criteria produce the corresponding field error.
     */
    @Test
    public void parse_emptyValues_rejectsInput() {
        assertParseFailure(parser, "n/ \t ", FilterCommand.MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "s/", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "l/", Level.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "s/Math n/ l/Secondary 3", FilterCommand.MESSAGE_EMPTY_NAME);
    }

    /**
     * Checks that unknown and repeated prefixes are rejected regardless of case.
     */
    @Test
    public void parse_unknownOrDuplicatePrefixes_rejectsInput() {
        assertParseFailure(parser, "n/Alice X/foo", "Unknown prefix: x/.");
        assertParseFailure(parser, "r/student", "Unknown prefix: r/.");
        assertParseFailure(parser, "n/Alice N/Bob", "Prefix n/ may be specified only once.");
        assertParseFailure(parser, "s/Math S/Math", "Prefix s/ may be specified only once.");
        assertParseFailure(parser, "l/Secondary 3 L/Secondary 4", "Prefix l/ may be specified only once.");
    }

    /**
     * Checks subject length boundaries and rejects invalid subject characters.
     */
    @Test
    public void parse_fieldLengthBoundaries_acceptsOnlyValidLengths() {
        for (int length : List.of(1, 40)) {
            String subject = "A".repeat(length);
            assertParseSuccess(parser, "s/ " + subject + " ", command(List.of(), subject, null));
        }
        assertParseFailure(parser, "s/" + "A".repeat(41), Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "s/Math!", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "s/Additional\tMathematics", Subject.MESSAGE_CONSTRAINTS);
    }

    /**
     * Checks that filters accept supported academic levels and reject unsupported stages and years.
     */
    @Test
    public void parse_level_acceptsOnlySupportedAcademicLevels() {
        for (String level : List.of("Primary 1", "Primary 6", "Secondary 1", "Secondary 5", "JC 1", "JC 2")) {
            assertParseSuccess(parser, "l/ " + level + " ", command(List.of(), null, level));
        }
        for (String invalid : List.of("Primary 7", "Secondary 6", "JC 5", "Secondary", "3", "Secondary!", "Adult")) {
            assertParseFailure(parser, "l/" + invalid, Level.MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * Checks that competing input errors follow the specified validation precedence.
     */
    @Test
    public void parse_multipleErrors_reportsSpecifiedPrecedence() {
        assertParseFailure(parser, "bad n/ n/Bob x/foo", "Unknown prefix: x/.");
        assertParseFailure(parser, "bad n/ n/Bob", "Prefix n/ may be specified only once.");
        assertParseFailure(parser, "l/ l/ s/ s/ n/ n/", "Prefix n/ may be specified only once.");
        assertParseFailure(parser, "bad n/ s/ l/", FilterCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "l/ s/ n/", FilterCommand.MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "l/ s/", Subject.MESSAGE_CONSTRAINTS);
    }

    /**
     * Checks that isolated Unicode blank tokens report parse errors instead of escaping predicate validation.
     */
    @Test
    public void parse_mixedUnicodeWhitespace_rejectsBlankKeywords() {
        for (String whitespace : List.of("\u2003", "\u2002", "\u3000")) {
            for (String name : List.of("Amy " + whitespace + " Tan", whitespace + " Amy", "Amy " + whitespace)) {
                assertParseFailure(parser, "n/" + name, FilterCommand.MESSAGE_INVALID_NAME_KEYWORD);
                assertParseFailure(parser, "n/" + name + " s/Mathematics l/Secondary 3",
                        FilterCommand.MESSAGE_INVALID_NAME_KEYWORD);
            }
            assertParseFailure(parser, "n/ " + whitespace + " ", FilterCommand.MESSAGE_EMPTY_NAME);
        }
    }

    private FilterCommand command(List<String> keywords, String subject, String level) {
        return new FilterCommand(new ContactMatchesCriteriaPredicate(keywords,
                Optional.ofNullable(subject).map(Subject::new), Optional.ofNullable(level).map(Level::new)));
    }
}
