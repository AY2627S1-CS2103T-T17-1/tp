package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.commons.core.index.Index.fromOneBased;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests sort composition, source-order preservation, and displayed-index command behavior.
 */
public class SortCommandTest {
    /**
     * Checks that sorting composes with filtering and list restores the original contact order.
     */
    @Test
    public void execute_filteredContacts_sortsDisplayAndListRestoresInsertionOrder() {
        Person later = new PersonBuilder().withName("Zoe").withLevel("JC 2").build();
        Person earlier = new PersonBuilder().withName("Amy").withLevel("Primary 1").build();
        Person parent = new PersonBuilder().withRole("parent").withName("Parent").build();
        ModelManager model = new ModelManager();
        model.addPerson(later);
        model.addPerson(parent);
        model.addPerson(earlier);
        Predicate<Person> students = person -> person.getLevel().isPresent();
        model.updateFilteredPersonList(students);
        AddressBook original = new AddressBook(model.getAddressBook());

        assertEquals(SortCommand.MESSAGE_SUCCESS, new SortCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(earlier, later), model.getFilteredPersonList());
        assertEquals(original, model.getAddressBook());
        assertFalse(model.getFilteredPersonPredicate().test(parent));
        assertFalse(new SortCommand().isDataChanging());

        model.updateFilteredPersonList(person -> true);
        assertEquals(List.of(earlier, later, parent), model.getFilteredPersonList());
        new SortCommand().execute(model);
        assertEquals(List.of(earlier, later, parent), model.getFilteredPersonList());
        new ListCommand().execute(model);
        assertEquals(List.of(later, parent, earlier), model.getFilteredPersonList());
    }

    /**
     * Checks that view, edit, and delete target the contact shown at the sorted index.
     */
    @Test
    public void execute_indexCommands_targetsDisplayedContact() throws Exception {
        Person later = new PersonBuilder().withName("Zoe").withLevel("JC 2").build();
        Person earlier = new PersonBuilder().withName("Amy").withLevel("Primary 1").build();
        ModelManager model = new ModelManager();
        model.addPerson(later);
        model.addPerson(earlier);
        new SortCommand().execute(model);

        assertEquals(earlier, new ViewCommand(fromOneBased(1)).execute(model).getContact().orElseThrow());
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setName(new Name("Alex"));
        new EditCommand(fromOneBased(1), descriptor).execute(model);
        assertEquals("Alex", model.getFilteredPersonList().get(0).getName().fullName);
        new DeleteCommand(fromOneBased(1)).execute(model);
        assertEquals(List.of(later), model.getAddressBook().getPersonList());
    }

    /**
     * Checks that empty and single-contact displays can be sorted successfully.
     */
    @Test
    public void execute_emptyOrSingleContact_succeeds() {
        ModelManager model = new ModelManager();
        assertEquals(SortCommand.MESSAGE_SUCCESS, new SortCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
        Person onlyContact = new PersonBuilder().withRole("parent").build();
        model.addPerson(onlyContact);
        new SortCommand().execute(model);
        assertEquals(List.of(onlyContact), model.getFilteredPersonList());
    }

    /**
     * Checks that later additions, replacements, and deletions retain the active ordering.
     */
    @Test
    public void execute_sourceChanges_keepSortingActive() {
        Person zoe = new PersonBuilder().withName("Zoe").withLevel("Secondary 3").build();
        Person ben = new PersonBuilder().withName("Ben").withLevel("Secondary 3").build();
        Person amy = new PersonBuilder().withName("Amy").withLevel("Secondary 3").build();
        ModelManager model = new ModelManager();
        model.addPerson(zoe);
        new SortCommand().execute(model);
        model.addPerson(ben);
        assertEquals(List.of(ben, zoe), model.getFilteredPersonList());
        model.setPerson(zoe, amy);
        assertEquals(List.of(amy, ben), model.getFilteredPersonList());
        Person earlier = new PersonBuilder(ben).withLevel("Primary 1").build();
        model.setPerson(ben, earlier);
        assertEquals(List.of(earlier, amy), model.getFilteredPersonList());
        model.deletePerson(earlier);
        assertEquals(List.of(amy), model.getFilteredPersonList());
    }
}
