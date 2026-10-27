package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Checks displayed indices across name-search workflows through the parser, model, and temporary JSON storage.
 */
public class DisplayedListIndexIntegrationTest {
    @TempDir
    private Path temporaryFolder;

    private Model model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;

    /**
     * Creates an independent contact list and saved file for each workflow.
     *
     * @throws IOException If the initial contacts cannot be saved.
     */
    @BeforeEach
    public void setUp() throws IOException {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json"));
        storage.saveAddressBook(model.getAddressBook());
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    /**
     * Checks that deletion renumbers search results and list restores only the remaining contacts in insertion order.
     *
     * @throws Exception If command execution or storage access fails unexpectedly.
     */
    @Test
    public void execute_searchThenDelete_usesUpdatedDisplayedIndices() throws Exception {
        List<Person> allContacts = List.of(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE);
        List<Person> searchResults = List.of(CARL, ELLE, FIONA);

        // These contacts occupy full-list positions 3, 5, and 6, but displayed positions 1, 2, and 3.
        logic.execute("find Kurz Elle Kunz");
        assertState(searchResults, allContacts);
        assertEquals(CommandResult.expandContact("Showing contact: Carl Kurz.", CARL), logic.execute("view 1"));
        assertState(searchResults, allContacts);
        assertEquals(CommandResult.expandContact("Showing contact: Fiona Kunz.", FIONA), logic.execute("view 3"));
        assertState(searchResults, allContacts);

        assertEquals("Deleted contact: student Carl Kurz (95352563).", logic.execute("delete 1").getFeedbackToUser());
        List<Person> afterFirstDelete = List.of(ALICE, BENSON, DANIEL, ELLE, FIONA, GEORGE);
        List<Person> remainingResults = List.of(ELLE, FIONA);
        assertState(remainingResults, afterFirstDelete);

        assertEquals(CommandResult.expandContact("Showing contact: Elle Meyer.", ELLE), logic.execute("view 1"));
        assertState(remainingResults, afterFirstDelete);
        assertEquals(CommandResult.expandContact("Showing contact: Fiona Kunz.", FIONA), logic.execute("view 2"));
        assertState(remainingResults, afterFirstDelete);

        // Displayed index 3 was valid before deletion and still identifies Daniel in the full list.
        assertInvalidIndex("view 3", 3, remainingResults, afterFirstDelete);
        assertInvalidIndex("delete 3", 3, remainingResults, afterFirstDelete);

        assertEquals("Deleted contact: student Elle Meyer (9482224).", logic.execute("delete 1").getFeedbackToUser());
        List<Person> afterSecondDelete = List.of(ALICE, BENSON, DANIEL, FIONA, GEORGE);
        assertState(List.of(FIONA), afterSecondDelete);
        assertEquals(CommandResult.expandContact("Showing contact: Fiona Kunz.", FIONA), logic.execute("view 1"));
        assertState(List.of(FIONA), afterSecondDelete);

        assertEquals(CommandResult.collapseContacts("Showing 5 contacts."), logic.execute("list"));
        assertState(afterSecondDelete, afterSecondDelete);
    }

    /**
     * Checks that an empty search rejects indexed commands without changing data and list restores the contacts.
     *
     * @throws Exception If command execution or storage access fails unexpectedly.
     */
    @Test
    public void execute_searchWithNoMatches_preservesDataAndRestoresList() throws Exception {
        List<Person> allContacts = List.of(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE);
        logic.execute("find Meier");
        assertState(List.of(BENSON, DANIEL), allContacts);

        logic.execute("find Nobody");
        assertState(List.of(), allContacts);
        assertInvalidIndex("view 1", 1, List.of(), allContacts);
        assertInvalidIndex("delete 1", 1, List.of(), allContacts);

        assertEquals(CommandResult.collapseContacts("Showing 7 contacts."), logic.execute("list"));
        assertState(allContacts, allContacts);
        assertEquals(CommandResult.expandContact("Showing contact: Alice Pauline.", ALICE), logic.execute("view 1"));
        assertState(allContacts, allContacts);
    }

    /**
     * Checks the displayed order and all remaining contacts in memory and storage against explicit expectations.
     *
     * @param expectedDisplayed Contacts expected in the displayed list, in order.
     * @param expectedAll Contacts expected in the complete address book, in insertion order.
     * @throws Exception If the saved contacts cannot be read.
     */
    private void assertState(List<Person> expectedDisplayed, List<Person> expectedAll) throws Exception {
        assertEquals(expectedDisplayed, logic.getFilteredPersonList());
        assertEquals(expectedAll, model.getAddressBook().getPersonList());
        assertEquals(expectedAll, storage.readAddressBook().orElseThrow().getPersonList());
    }

    /**
     * Checks that an invalid displayed index preserves the current results, all contacts, and the saved file.
     *
     * @param command Command string containing the invalid index.
     * @param index Invalid one-based displayed index.
     * @param expectedDisplayed Contacts expected to remain displayed, in order.
     * @param expectedAll Contacts expected to remain in the complete address book, in insertion order.
     * @throws Exception If the saved contacts cannot be read.
     */
    private void assertInvalidIndex(String command, int index, List<Person> expectedDisplayed,
            List<Person> expectedAll) throws Exception {
        String savedData = Files.readString(storage.getAddressBookFilePath());
        assertThrows(CommandException.class, "No contact exists at index " + index + " in the current list.", () -> {
            logic.execute(command);
        });
        assertState(expectedDisplayed, expectedAll);
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
    }
}
