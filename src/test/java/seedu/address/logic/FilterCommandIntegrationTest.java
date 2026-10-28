package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonNameComparator;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises focused filtering through real parsing, displayed-list transformations, and JSON storage.
 */
public class FilterCommandIntegrationTest {
    private static final Person ZOE = new PersonBuilder().withName("Zoe Tan").build();
    private static final Person PARENT = new PersonBuilder().withName("Alice Tan").withRole("parent").build();
    private static final Person AMY = new PersonBuilder().withName("Amy Tan").build();
    private static final Person BOB = new PersonBuilder().withName("Bob Lee")
            .withSubject("Physics").withLevel("JC 2").build();
    private static final Person AMY_LOWER = new PersonBuilder().withName("amy tan")
            .withPhone("98765432").withEmail("another.amy@example.com").build();
    private static final List<Person> INSERTION_ORDER = List.of(ZOE, PARENT, AMY, BOB, AMY_LOWER);

    @TempDir
    Path temporaryFolder;

    private Model model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;
    private int saveCount;
    private boolean failSave;

    /**
     * Creates a saved contact fixture with storage that counts writes and can simulate a save failure.
     *
     * @throws IOException If the initial contact file cannot be written.
     */
    @BeforeEach
    public void setUp() throws IOException {
        model = new ModelManager();
        INSERTION_ORDER.forEach(model::addPerson);
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json")) {
            /**
             * Saves contacts and counts successful writes unless a simulated failure is enabled.
             *
             * @param addressBook Contacts to save.
             * @throws IOException If failure simulation is enabled or the file cannot be written.
             */
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                if (failSave) {
                    throw new IOException("Test save failure");
                }
                super.saveAddressBook(addressBook);
                saveCount++;
            }
        };
        storage.saveAddressBook(model.getAddressBook());
        saveCount = 0;
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    /**
     * Checks stable name ordering, the unchanged observable view, and the absence of storage writes.
     *
     * @throws Exception If command execution or reading the saved fixture unexpectedly fails.
     */
    @Test
    public void execute_combinedFilter_sortsNamesWithStableTiesAndDoesNotSave() throws Exception {
        String saved = Files.readString(storage.getAddressBookFilePath());
        ObservableList<Person> observedList = logic.getFilteredPersonList();
        assertEquals("Showing 3 matching contacts.",
                logic.execute("FiLtEr S/mathematics L/secondary   3").getFeedbackToUser());
        assertSame(observedList, logic.getFilteredPersonList());
        assertEquals(List.of(AMY, AMY_LOWER, ZOE), observedList);
        assertEquals(INSERTION_ORDER, model.getAddressBook().getPersonList());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        assertEquals(0, saveCount);
        assertThrows(UnsupportedOperationException.class, () -> observedList.remove(0));
    }

    /**
     * Checks that filtering replaces previous searches while existing name searches retain OR matching.
     *
     * @throws Exception If executing a valid search or filter unexpectedly fails.
     */
    @Test
    public void execute_filterAfterFindAndFilter_replacesPredicateAndPreservesFindOr() throws Exception {
        logic.execute("find Zoe");
        logic.execute("filter s/Physics");
        assertEquals(List.of(BOB), logic.getFilteredPersonList());
        logic.execute("filter n/Amy Tan");
        assertEquals(List.of(AMY, AMY_LOWER), logic.getFilteredPersonList());
        logic.execute("filter n/Amy Lee");
        assertTrue(logic.getFilteredPersonList().isEmpty());
        logic.execute("find Amy Lee");
        assertEquals(List.of(AMY, AMY_LOWER, BOB), logic.getFilteredPersonList());
    }

    /**
     * Checks that filtering retains an active comparator and listing resets both membership and order.
     *
     * @throws Exception If executing a valid filter or list command unexpectedly fails.
     */
    @Test
    public void execute_filterWithActiveComparator_preservesOrderingAndListClearsBoth() throws Exception {
        Comparator<Person> descendingNames = new PersonNameComparator().reversed();
        model.setPersonListComparator(descendingNames);
        logic.execute("filter s/Mathematics");
        assertSame(descendingNames, model.getPersonListComparator());
        assertEquals(List.of(ZOE, AMY, AMY_LOWER), logic.getFilteredPersonList());
        logic.execute("list");
        assertEquals(INSERTION_ORDER, logic.getFilteredPersonList());
        assertNull(model.getPersonListComparator());
        logic.execute("filter s/Mathematics");
        assertEquals(List.of(AMY, AMY_LOWER, ZOE), logic.getFilteredPersonList());
    }

    /**
     * Checks that sorting a filtered list retains membership and uses sorted indices without saving.
     *
     * @throws Exception If executing valid commands or reading the saved fixture unexpectedly fails.
     */
    @Test
    public void execute_sortAfterFilter_preservesMembershipAndDoesNotSave() throws Exception {
        String saved = Files.readString(storage.getAddressBookFilePath());
        ObservableList<Person> displayed = logic.getFilteredPersonList();
        logic.execute("filter n/Tan");
        assertEquals(List.of(PARENT, AMY, AMY_LOWER, ZOE), displayed);
        logic.execute("sort level");
        assertEquals(List.of(AMY, AMY_LOWER, ZOE, PARENT), displayed);
        assertSame(displayed, logic.getFilteredPersonList());
        assertEquals(PARENT, logic.execute("view 4").getContact().orElseThrow());
        assertEquals(INSERTION_ORDER, model.getAddressBook().getPersonList());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        assertEquals(0, saveCount);
    }

    /**
     * Checks that filtering retains academic ordering and listing restores all contacts in insertion order.
     *
     * @throws Exception If executing valid commands or reading the saved fixture unexpectedly fails.
     */
    @Test
    public void execute_filterAfterSort_retainsAcademicOrderAndListResetsBoth() throws Exception {
        String saved = Files.readString(storage.getAddressBookFilePath());
        logic.execute("sort level");
        assertEquals(List.of(AMY, AMY_LOWER, ZOE, BOB, PARENT), logic.getFilteredPersonList());
        Comparator<? super Person> comparator = model.getPersonListComparator();
        logic.execute("filter n/Tan");
        assertSame(comparator, model.getPersonListComparator());
        assertEquals(List.of(AMY, AMY_LOWER, ZOE, PARENT), logic.getFilteredPersonList());
        logic.execute("list");
        assertEquals(INSERTION_ORDER, logic.getFilteredPersonList());
        assertNull(model.getPersonListComparator());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        assertEquals(0, saveCount);
    }

    /**
     * Checks displayed indices before and after deletion and verifies the saved remaining contacts.
     *
     * @throws Exception If an expected successful command or storage read fails.
     */
    @Test
    public void execute_viewAndDelete_useSortedFilteredIndices() throws Exception {
        logic.execute("filter s/Mathematics");
        assertEquals(AMY, logic.execute("view 1").getContact().orElseThrow());
        assertEquals(ZOE, logic.execute("view 3").getContact().orElseThrow());
        assertEquals("Deleted contact: student Amy Tan (" + AMY.getPhone().value + ").",
                logic.execute("delete 1").getFeedbackToUser());
        assertEquals(List.of(AMY_LOWER, ZOE), logic.getFilteredPersonList());
        assertEquals(AMY_LOWER, logic.execute("view 1").getContact().orElseThrow());
        assertThrows(CommandException.class, "No contact exists at index 3 in the current list.", () ->
                logic.execute("delete 3"));
        assertEquals(List.of(ZOE, PARENT, BOB, AMY_LOWER), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        logic.execute("list");
        assertEquals(List.of(ZOE, PARENT, BOB, AMY_LOWER), logic.getFilteredPersonList());
        assertEquals(1, saveCount);
    }

    /**
     * Checks invalid indices in an empty result and restoration of the unchanged contacts by listing.
     *
     * @throws Exception If an expected successful filter or list command fails.
     */
    @Test
    public void execute_noMatches_preservesContactsAndListRestoresInsertionOrder() throws Exception {
        assertEquals("No matching contacts.", logic.execute("filter s/Chemistry").getFeedbackToUser());
        assertThrows(CommandException.class, "No contact exists at index 1 in the current list.", () ->
                logic.execute("view 1"));
        assertThrows(CommandException.class, "No contact exists at index 1 in the current list.", () ->
                logic.execute("delete 1"));
        logic.execute("list");
        assertEquals(INSERTION_ORDER, logic.getFilteredPersonList());
        assertEquals(0, saveCount);
    }

    /**
     * Checks that rejected filters preserve the displayed contacts, comparator, and saved data.
     *
     * @throws Exception If the initial filter or reading the saved fixture unexpectedly fails.
     */
    @Test
    public void execute_invalidFilter_preservesDisplayComparatorAndSavedData() throws Exception {
        logic.execute("filter s/Mathematics");
        List<Person> before = List.copyOf(logic.getFilteredPersonList());
        Comparator<? super Person> comparator = model.getPersonListComparator();
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String command : List.of("filter", "filter Alice", "filter n/", "filter s/", "filter l/",
                "filter x/foo", "filter s/Math S/Physics", "filter l/JC 5", "filter l/Secondary 6")) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(before, logic.getFilteredPersonList());
            assertSame(comparator, model.getPersonListComparator());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
        assertEquals(0, saveCount);
    }

    /**
     * Checks that a failed deletion restores contacts without losing the active filter or ordering.
     *
     * @throws Exception If the initial filter or reading the saved fixture unexpectedly fails.
     */
    @Test
    public void execute_failedDelete_restoresFilterOrderingAndStoredData() throws Exception {
        logic.execute("filter s/Mathematics");
        String saved = Files.readString(storage.getAddressBookFilePath());
        failSave = true;
        assertThrows(CommandException.class, LogicManager.MESSAGE_DELETE_SAVE_FAILURE, () -> logic.execute("delete 1"));
        assertEquals(List.of(AMY, AMY_LOWER, ZOE), logic.getFilteredPersonList());
        assertEquals(INSERTION_ORDER, model.getAddressBook().getPersonList());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        assertEquals(0, saveCount);
    }

    /**
     * Checks that editing targets a displayed index and clears filtering while preserving ordering.
     *
     * @throws Exception If executing the filter or edit unexpectedly fails.
     */
    @Test
    public void execute_edit_usesDisplayedIndexAndRetainsOrderingAfterClearingFilter() throws Exception {
        logic.execute("filter s/Mathematics");
        logic.execute("edit 1 n/Aaron Tan");
        Person edited = new PersonBuilder(AMY).withName("Aaron Tan").build();
        assertEquals(List.of(edited, PARENT, AMY_LOWER, BOB, ZOE), logic.getFilteredPersonList());
        assertEquals(List.of(ZOE, PARENT, edited, BOB, AMY_LOWER), model.getAddressBook().getPersonList());
    }

    /**
     * Checks that adding a contact clears filtering while retaining name order.
     *
     * @throws Exception If executing the filter or add command unexpectedly fails.
     */
    @Test
    public void execute_add_clearsFilterAndRetainsOrdering() throws Exception {
        logic.execute("filter s/Physics");
        logic.execute("add r/parent n/Aaron Lim p/91234567 e/aaron@example.com a/1 Road");
        assertEquals(6, logic.getFilteredPersonList().size());
        Person added = model.getAddressBook().getPersonList().getLast();
        assertEquals(added, logic.getFilteredPersonList().getFirst());
        assertEquals("Aaron Lim", added.getName().fullName);
    }

    /**
     * Checks that reloading saved contacts restores insertion order without session filtering.
     *
     * @throws Exception If filtering or reading the saved fixture unexpectedly fails.
     */
    @Test
    public void execute_reload_doesNotPersistFilterOrOrdering() throws Exception {
        logic.execute("filter s/Mathematics");
        Model restored = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(INSERTION_ORDER, restored.getFilteredPersonList());
        assertNull(restored.getPersonListComparator());
    }

    /**
     * Checks that filtering an empty address book reports no matches without creating a data file.
     *
     * @throws Exception If executing the valid filter unexpectedly fails.
     */
    @Test
    public void execute_emptyAddressBook_reportsNoMatchesWithoutCreatingFile() throws Exception {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        JsonAddressBookStorage emptyStorage = new JsonAddressBookStorage(temporaryFolder.resolve("empty.json"));
        LogicManager emptyLogic = new LogicManager(emptyModel, new StorageManager(emptyStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("empty-prefs.json"))));
        assertEquals(FilterCommand.MESSAGE_NO_MATCHES, emptyLogic.execute("filter n/Amy").getFeedbackToUser());
        assertTrue(Files.notExists(emptyStorage.getAddressBookFilePath()));
    }
}
