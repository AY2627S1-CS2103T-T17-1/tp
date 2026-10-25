package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ViewCommand}.
 */
public class ViewCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person contactToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String expectedMessage = "Showing contact: " + contactToView.getName().fullName + ".";

        // Viewing must leave the model unchanged.
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ViewCommand(INDEX_FIRST_PERSON), model,
                CommandResult.selectContact(expectedMessage, contactToView), expectedModel);
    }

    @Test
    public void execute_lastContact_success() {
        Index lastIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        Person contactToView = model.getFilteredPersonList().get(lastIndex.getZeroBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ViewCommand(lastIndex), model,
                CommandResult.selectContact("Showing contact: " + contactToView.getName().fullName + ".",
                        contactToView), expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertCommandFailure(new ViewCommand(outOfBoundIndex), model,
                "No contact exists at index " + outOfBoundIndex.getOneBased() + " in the current list.");
    }

    @Test
    public void execute_validIndexFilteredList_viewsDisplayedContact() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        // The first displayed contact is the second contact in the full list.
        Person contactToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String expectedMessage = "Showing contact: " + contactToView.getName().fullName + ".";

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(new ViewCommand(INDEX_FIRST_PERSON), model,
                CommandResult.selectContact(expectedMessage, contactToView), expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        assertCommandFailure(new ViewCommand(outOfBoundIndex), model,
                "No contact exists at index " + outOfBoundIndex.getOneBased() + " in the current list.");
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        model = new ModelManager();

        assertCommandFailure(new ViewCommand(INDEX_FIRST_PERSON), model,
                "No contact exists at index 1 in the current list.");
    }

    @Test
    public void isDataChanging_returnsFalse() {
        assertFalse(new ViewCommand(INDEX_FIRST_PERSON).isDataChanging());
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(INDEX_FIRST_PERSON);
        ViewCommand viewSecondCommand = new ViewCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(viewFirstCommand.equals(viewFirstCommand));

        // same values -> returns true
        ViewCommand viewFirstCommandCopy = new ViewCommand(INDEX_FIRST_PERSON);
        assertTrue(viewFirstCommand.equals(viewFirstCommandCopy));

        // different types -> returns false
        assertFalse(viewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(viewFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(viewFirstCommand.equals(viewSecondCommand));

        // different command with the same index -> returns false
        assertFalse(viewFirstCommand.equals(new DeleteCommand(INDEX_FIRST_PERSON)));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ViewCommand viewCommand = new ViewCommand(targetIndex);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, viewCommand.toString());
    }
}
