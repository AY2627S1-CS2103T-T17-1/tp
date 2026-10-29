package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the contact descriptions used in command feedback.
 */
public class MessagesTest {

    @Test
    public void format_studentWithAllFields_includesSubjectLevelNoteAndTags() {
        assertEquals("student: Alice Pauline (94351253); Email: alice@example.com; "
                + "Address: 123, Jurong West Ave 6, #08-111; Subject: Mathematics; Level: Secondary 3; "
                + "Note: Prefers visual examples; Tags: friends", Messages.format(ALICE));
    }

    @Test
    public void format_multipleTags_sortsAndSeparatesThem() {
        Person taggedParent = new PersonBuilder(GEORGE).withTags("weekend", "Alpha", "evening").build();

        assertEquals("parent: George Best (9482442); Email: anna@example.com; Address: 4th street; "
                + "Note: Contact after 6 pm; Tags: Alpha, evening, weekend", Messages.format(taggedParent));
    }

    @Test
    public void format_parent_omitsSubjectLevelAndEmptyTags() {
        assertEquals("parent: George Best (9482442); Email: anna@example.com; Address: 4th street; "
                + "Note: Contact after 6 pm", Messages.format(GEORGE));
    }

    @Test
    public void format_studentWithoutOptionalFields_showsNotRecordedAndNoNote() {
        Person legacyStudent = new PersonBuilder(ALICE).withoutSubject().withoutLevel().withoutNote().withTags()
                .build();

        assertEquals("student: Alice Pauline (94351253); Email: alice@example.com; "
                + "Address: 123, Jurong West Ave 6, #08-111; Subject: Not recorded; Level: Not recorded; "
                + "Note: None", Messages.format(legacyStudent));
    }
}
