package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonLevelComparator;
import seedu.address.model.person.PersonNameComparator;
import seedu.address.testutil.AddressBookBuilder;

/**
 * Tests model state and the independent filtering and ordering of the observable displayed list.
 */
public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void addLink_validLink_linksContacts() {
        modelManager.addPerson(GEORGE);
        modelManager.addPerson(ALICE);
        ParentStudentLink link = ParentStudentLink.between(GEORGE, ALICE);

        modelManager.addLink(link);

        assertTrue(modelManager.hasLink(link));
        assertEquals(List.of(ALICE), modelManager.getLinkedPersons(GEORGE));
    }

    @Test
    public void deletePerson_linkedStudent_removesLink() {
        modelManager.addPerson(GEORGE);
        modelManager.addPerson(ALICE);
        ParentStudentLink link = ParentStudentLink.between(GEORGE, ALICE);
        modelManager.addLink(link);

        modelManager.deletePerson(ALICE);

        assertFalse(modelManager.hasLink(link));
        assertEquals(List.of(), modelManager.getLinkedPersons(GEORGE));
    }

    @Test
    public void hasLink_nullLink_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasLink(null));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    /**
     * Checks that display ordering preserves membership, stored order, and the observable list instance.
     */
    @Test
    public void setPersonListComparator_preservesFilterAndStoredOrder() {
        modelManager.addPerson(BENSON);
        modelManager.addPerson(ALICE);
        ObservableList<Person> displayed = modelManager.getFilteredPersonList();
        modelManager.updateFilteredPersonList(ALICE::equals);
        modelManager.setPersonListComparator(new PersonNameComparator());
        assertEquals(List.of(ALICE), displayed);
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        assertEquals(List.of(ALICE, BENSON), displayed);
        assertEquals(List.of(BENSON, ALICE), modelManager.getAddressBook().getPersonList());
        modelManager.setPersonListComparator(null);
        assertEquals(List.of(BENSON, ALICE), displayed);
        assertSame(displayed, modelManager.getFilteredPersonList());
        assertNull(modelManager.getPersonListComparator());
    }

    /**
     * Checks that models with different displayed orders are not equal.
     */
    @Test
    public void equals_differentDisplayedOrder_returnsFalse() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(BENSON).withPerson(ALICE).build();
        ModelManager ordered = new ModelManager(addressBook, new UserPrefs());
        ModelManager insertion = new ModelManager(addressBook, new UserPrefs());
        ordered.setPersonListComparator(new PersonNameComparator());
        assertFalse(ordered.equals(insertion));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    /**
     * Checks that sorting updates the existing observable view used by UI bindings.
     */
    @Test
    public void setPersonListComparator_levelOrder_preservesObservableListIdentity() {
        ModelManager model = new ModelManager();
        model.addPerson(BENSON);
        model.addPerson(ALICE);
        var displayed = model.getFilteredPersonList();
        model.setPersonListComparator(new PersonLevelComparator());
        assertTrue(displayed == model.getFilteredPersonList());
        assertEquals(List.of(ALICE, BENSON), displayed);
    }
}
