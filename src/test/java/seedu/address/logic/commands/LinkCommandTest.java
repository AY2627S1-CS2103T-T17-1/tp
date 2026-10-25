package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code LinkCommand}.
 */
public class LinkCommandTest {

    /** GEORGE is the only parent in the typical address book and is displayed last. */
    private static final Index INDEX_GEORGE = Index.fromOneBased(7);

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_parentAndStudent_success() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addLink(ParentStudentLink.between(GEORGE, ALICE));
        String expectedMessage = "Linked parent George Best (9482442) to student Alice Pauline (94351253).";

        assertCommandSuccess(new LinkCommand(INDEX_GEORGE, INDEX_FIRST_PERSON), model,
                CommandResult.selectContact(expectedMessage, GEORGE), expectedModel);
        assertEquals(List.of(GEORGE), model.getLinkedPersons(ALICE));
    }

    @Test
    public void execute_duplicateLink_throwsCommandException() {
        model.addLink(ParentStudentLink.between(GEORGE, ALICE));

        assertCommandFailure(new LinkCommand(INDEX_GEORGE, INDEX_FIRST_PERSON), model,
                "George Best (9482442) is already linked to Alice Pauline (94351253).");
    }

    @Test
    public void execute_firstContactNotParent_throwsCommandException() {
        assertCommandFailure(new LinkCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), model,
                "The contact at index 1 is not a parent.");
    }

    @Test
    public void execute_secondContactNotStudent_throwsCommandException() {
        model.addPerson(new PersonBuilder(GEORGE).withName("Grace Best").withPhone("81234567")
                .withEmail("grace@example.com").build());

        assertCommandFailure(new LinkCommand(INDEX_GEORGE, Index.fromOneBased(8)), model,
                "The contact at index 8 is not a student.");
    }

    @Test
    public void execute_sameIndex_throwsCommandException() {
        assertCommandFailure(new LinkCommand(INDEX_GEORGE, INDEX_GEORGE), model,
                LinkCommand.MESSAGE_SAME_INDEX);
    }

    @Test
    public void execute_indexOutOfRange_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertCommandFailure(new LinkCommand(INDEX_GEORGE, outOfBoundIndex), model,
                "No contact exists at index " + outOfBoundIndex.getOneBased() + " in the current list.");
    }

    @Test
    public void equals() {
        LinkCommand linkFirstSecond = new LinkCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);

        assertTrue(linkFirstSecond.equals(linkFirstSecond));
        assertTrue(linkFirstSecond.equals(new LinkCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
        assertFalse(linkFirstSecond.equals(new LinkCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON)));
        assertFalse(linkFirstSecond.equals(null));
        assertFalse(linkFirstSecond.equals(1));
    }

    @Test
    public void toStringMethod() {
        LinkCommand linkCommand = new LinkCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        String expected = LinkCommand.class.getCanonicalName() + "{parentIndex=" + INDEX_FIRST_PERSON
                + ", studentIndex=" + INDEX_SECOND_PERSON + "}";

        assertEquals(expected, linkCommand.toString());
    }
}
