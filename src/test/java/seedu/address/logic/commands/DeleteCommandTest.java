package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
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
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_linkedStudent_removesLinksAndReportsCount() {
        model.addLink(ParentStudentLink.between(GEORGE, ALICE));
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(new DeleteCommand(INDEX_FIRST_PERSON), model,
                "Deleted contact: student Alice Pauline (94351253). Removed 1 link(s).", expectedModel);
        assertEquals(List.of(), model.getLinkedPersons(GEORGE));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = "Deleted contact: student Alice Pauline (94351253).";

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model,
                "No contact exists at index " + outOfBoundIndex.getOneBased() + " in the current list.");
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = "Deleted contact: student Benson Meier (98765432).";

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model,
                "No contact exists at index " + outOfBoundIndex.getOneBased() + " in the current list.");
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        model = new ModelManager();

        assertCommandFailure(new DeleteCommand(INDEX_FIRST_PERSON), model,
                "No contact exists at index 1 in the current list.");
    }

    @Test
    public void execute_lastContact_success() {
        Index lastIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        Person contactToDelete = model.getFilteredPersonList().get(lastIndex.getZeroBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(contactToDelete);

        assertCommandSuccess(new DeleteCommand(lastIndex), model,
                "Deleted contact: parent George Best (9482442).", expectedModel);
    }

    @Test
    public void execute_sameNameAndPhoneAcrossRoles_identifiesDeletedRole() {
        Person student = new PersonBuilder(ALICE)
                .withName("Alex Tan").withPhone("91234567").withEmail("alex.student@example.com").build();
        Person parent = new PersonBuilder(GEORGE)
                .withName("Alex Tan").withPhone("91234567").withEmail("alex.parent@example.com").build();
        model = new ModelManager();
        model.addPerson(student);
        model.addPerson(parent);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(parent);

        assertCommandSuccess(new DeleteCommand(INDEX_SECOND_PERSON), model,
                "Deleted contact: parent Alex Tan (91234567).", expectedModel);
    }

    @Test
    public void execute_sameNameDifferentPhones_identifiesDeletedPhone() {
        Person firstStudent = new PersonBuilder(ALICE)
                .withName("Alex Tan").withPhone("93333333").withEmail("alex.first@example.com").build();
        Person secondStudent = new PersonBuilder(ALICE)
                .withName("Alex Tan").withPhone("92222222").withEmail("alex.second@example.com").build();
        model = new ModelManager();
        model.addPerson(firstStudent);
        model.addPerson(secondStudent);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(firstStudent);

        assertCommandSuccess(new DeleteCommand(INDEX_FIRST_PERSON), model,
                "Deleted contact: student Alex Tan (93333333).", expectedModel);
        assertEquals(List.of(secondStudent), model.getFilteredPersonList());
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
