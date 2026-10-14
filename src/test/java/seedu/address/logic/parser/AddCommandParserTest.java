package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Subject;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the MVP add grammar, role-specific rules, normalization, and exact errors.
 */
public class AddCommandParserTest {
    private static final String BASE = " r/student n/Alex Tan p/9123 4567 e/alex@example.com a/1 Road";
    private static final String STUDENT_FIELDS = " s/Mathematics l/Secondary 3";
    private static final String PARENT = " r/parent n/May Tan p/+65 9876-5432 e/may@example.com a/1 Road";

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_validStudent_success() {
        Person expected = new PersonBuilder().withName("Alex Tan").withPhone("9123 4567")
                .withEmail("alex@example.com").withAddress("1 Road").build();
        assertParseSuccess(parser, BASE + STUDENT_FIELDS, new AddCommand(expected));
    }

    @Test
    public void parse_reorderedMixedCaseFields_normalizesAndPreservesCapitalization() {
        Person expected = new PersonBuilder().withName("Alex Tan").withPhone("9123 4567")
                .withEmail("Alex@Example.com").withAddress("1 Road").withSubject("A-Math")
                .withLevel("Secondary 3").withNote("Discuss x/foo").build();
        assertParseSuccess(parser, " O/Discuss \\x/foo L/Secondary   3 S/A-Math A/1 Road"
                + " E/Alex@Example.com P/9123 4567 N/Alex   Tan R/STUDENT", new AddCommand(expected));
    }

    @Test
    public void parse_parentWithOptionalNote_success() {
        Person expected = new PersonBuilder().withRole("parent").withName("May Tan").withPhone("+65 9876-5432")
                .withEmail("may@example.com").withAddress("1 Road").withNote("Contact after 6 pm").build();
        assertParseSuccess(parser, PARENT + " o/Contact after 6 pm", new AddCommand(expected));
    }

    @Test
    public void parse_emptyOrOmittedNote_success() {
        Person expected = new PersonBuilder().withRole("parent").withName("May Tan").withPhone("+65 9876-5432")
                .withEmail("may@example.com").withAddress("1 Road").build();
        assertParseSuccess(parser, PARENT, new AddCommand(expected));
        assertParseSuccess(parser, PARENT + " o/  ", new AddCommand(expected));
    }

    @Test
    public void parse_missingRequiredPrefix_failure() {
        String[] fields = {" r/student", " n/Alex Tan", " p/91234567", " e/alex@example.com", " a/1 Road"};
        for (int omitted = 0; omitted < fields.length; omitted++) {
            StringBuilder input = new StringBuilder();
            for (int field = 0; field < fields.length; field++) {
                if (field != omitted) {
                    input.append(fields[field]);
                }
            }
            assertParseFailure(parser, input + STUDENT_FIELDS, AddCommand.MESSAGE_INVALID_FORMAT);
        }
        assertParseFailure(parser, "", AddCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "unexpected" + BASE + STUDENT_FIELDS, AddCommand.MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_studentMissingSubjectOrLevel_failure() {
        assertParseFailure(parser, BASE, AddCommand.MESSAGE_MISSING_STUDENT_FIELDS);
        assertParseFailure(parser, BASE + " s/Mathematics", AddCommand.MESSAGE_MISSING_STUDENT_FIELDS);
        assertParseFailure(parser, BASE + " l/Secondary 3", AddCommand.MESSAGE_MISSING_STUDENT_FIELDS);
    }

    @Test
    public void parse_parentWithStudentPrefixes_failureEvenWhenEmpty() {
        assertParseFailure(parser, PARENT + " s/Mathematics", AddCommand.MESSAGE_PARENT_FIELDS);
        assertParseFailure(parser, PARENT + " l/Secondary 3", AddCommand.MESSAGE_PARENT_FIELDS);
        assertParseFailure(parser, PARENT + " s/ l/", AddCommand.MESSAGE_PARENT_FIELDS);
    }

    @Test
    public void parse_emptyRequiredValues_failure() {
        assertParseFailure(parser, BASE.replace("r/student", "r/") + STUDENT_FIELDS, Role.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE.replace("n/Alex Tan", "n/") + STUDENT_FIELDS, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE.replace("p/9123 4567", "p/") + STUDENT_FIELDS, Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE.replace("e/alex@example.com", "e/") + STUDENT_FIELDS,
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE.replace("a/1 Road", "a/") + STUDENT_FIELDS, Address.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE + " s/ l/Secondary 3", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE + " s/Mathematics l/", Level.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, BASE.replace("r/student", "r/teacher") + STUDENT_FIELDS, Role.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE.replace("n/Alex Tan", "n/Alex&") + STUDENT_FIELDS, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE.replace("p/9123 4567", "p/12") + STUDENT_FIELDS, Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE.replace("e/alex@example.com", "e/alex@localhost") + STUDENT_FIELDS,
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE.replace("a/1 Road", "a/" + "a".repeat(151)) + STUDENT_FIELDS,
                Address.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE + " s/Math* l/Secondary 3", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE + " s/Math l/Secondary.3", Level.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, BASE + STUDENT_FIELDS + " o/" + "n".repeat(201), Note.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_eachRepeatedPrefix_failure() {
        String[] duplicates = {"r/student", "n/Bob", "p/91234567", "e/bob@example.com",
            "a/2 Road", "s/Physics", "l/JC 1", "o/Second note"};
        for (String duplicate : duplicates) {
            assertParseFailure(parser, BASE + STUDENT_FIELDS + " o/First note " + duplicate,
                    "Prefix " + duplicate.substring(0, 2) + " may be specified only once.");
        }
        assertParseFailure(parser, BASE + STUDENT_FIELDS + " N/Bob", "Prefix n/ may be specified only once.");
    }

    @Test
    public void parse_unknownPrefixBeforeOtherErrors_failure() {
        assertParseFailure(parser, " n/Alex x/foo", "Unknown prefix: x/.");
        assertParseFailure(parser, " n/Alex N/Bob X/foo", "Unknown prefix: x/.");
        assertParseFailure(parser, BASE + STUDENT_FIELDS + " t/friend", "Unknown prefix: t/.");
        assertParseFailure(parser, BASE + STUDENT_FIELDS + " o/Discuss x/foo", "Unknown prefix: x/.");
    }

    @Test
    public void parse_repeatedPrefixBeforeMissingFields_failure() {
        assertParseFailure(parser, " n/Alex N/Bob", "Prefix n/ may be specified only once.");
    }

    @Test
    public void parse_literalPrefixesAndSubjectSlash_success() {
        Person expected = new PersonBuilder().withName("Alex Tan").withPhone("9123 4567")
                .withEmail("alex@example.com").withAddress("Block x/12 and n/3")
                .withSubject("Biology/Chemistry").build();
        assertParseSuccess(parser, BASE.replace("a/1 Road", "a/Block \\x/12 and \\n/3")
                + " s/Biology/Chemistry l/Secondary 3", new AddCommand(expected));
    }
}
