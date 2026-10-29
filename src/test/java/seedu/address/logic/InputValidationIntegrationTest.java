package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks input validation and Unicode handling through real commands and JSON persistence.
 */
public class InputValidationIntegrationTest {
    private static final String PARENT = "add r/parent n/Alpha Parent p/91000001 e/alpha@example.com"
            + " a/1 Test Road o/Original note";
    private static final String STUDENT = "add r/student n/Alpha Student p/91000002 e/student@example.com"
            + " a/2 Test Road s/Mathematics l/Secondary 3";

    /**
     * Holds fictional contacts and preferences for each independent scenario.
     */
    @TempDir
    public Path temporaryFolder;

    private Model model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;

    /**
     * Creates the real parser, model, and storage components with an empty contact collection.
     */
    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    /**
     * Checks that rejected address changes preserve contacts, links, the filtered and sorted view, and JSON.
     *
     * @throws Exception If fixture setup or a saved-file assertion fails.
     */
    @Test
    public void execute_addressWithoutContent_rejectsAddAndEditWithoutChanges() throws Exception {
        logic.execute(PARENT);
        logic.execute(STUDENT);
        logic.execute("link 1 2");
        logic.execute("filter n/Alpha");
        logic.execute("sort level");
        String[] addresses = {"\u200B", "\u200D\uFEFF", "\u00A0\u2003", "\u0301\uFE0F"};
        for (String address : addresses) {
            String[] commands = {"edit 2 a/" + address,
                "add r/parent n/Other Parent p/91000003 e/other@example.com a/" + address};
            for (String command : commands) {
                ParseException exception = assertRejectedWithoutChanges(command, ParseException.class);
                assertEquals(Address.MESSAGE_CONSTRAINTS, exception.getMessage());
            }
        }
        assertEquals("Showing 2 contacts.", logic.execute("list").getFeedbackToUser());
    }

    /**
     * Checks that Unicode letters, marks, and format characters survive add, edit, and JSON reloads.
     *
     * @throws Exception If a valid command or storage round trip unexpectedly fails.
     */
    @Test
    public void execute_unicodeAddress_preservesContentAndLinksAcrossReloads() throws Exception {
        String address = "Cafe\u0301\u200D 1 Road";
        logic.execute(PARENT.replace("1 Test Road", address));
        logic.execute(STUDENT);
        logic.execute("link 1 2");
        assertEquals(address, storage.readAddressBook().orElseThrow().getPersonList().getFirst().getAddress().value);

        String editedAddress = "شارع\u200C 12";
        logic.execute("edit 1 a/" + editedAddress);
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();
        assertEquals(editedAddress, restored.getPersonList().getFirst().getAddress().value);
        assertEquals(model.getAddressBook(), restored);
        assertEquals(1, restored.getLinkList().size());
    }

    /**
     * Checks long email addition, editing, duplicate identity, rejection, and persistence.
     *
     * @throws Exception If a valid command or storage operation unexpectedly fails.
     */
    @Test
    public void execute_longEmail_handlesAddEditDuplicatesAndReload() throws Exception {
        String domain = "a.".repeat(2500) + "com";
        String firstEmail = "a@" + domain;
        logic.execute(PARENT.replace("alpha@example.com", firstEmail));
        assertEquals(firstEmail, model.getAddressBook().getPersonList().getFirst().getEmail().value);
        String editedEmail = "b@" + domain;
        logic.execute("edit 1 e/" + editedEmail);
        assertEquals(editedEmail, model.getAddressBook().getPersonList().getFirst().getEmail().value);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        logic.execute("filter n/Alpha");
        logic.execute("sort level");
        ParseException invalidEdit = assertRejectedWithoutChanges("edit 1 e/" + editedEmail + ".",
                ParseException.class);
        assertEquals(Email.MESSAGE_CONSTRAINTS, invalidEdit.getMessage());
        assertRejectedWithoutChanges("add r/parent n/Other Parent p/91000003 e/" + firstEmail
                + ".- a/3 Road", ParseException.class);
        assertRejectedWithoutChanges("add r/parent n/alpha parent p/91000003 e/"
                + editedEmail.toUpperCase(Locale.ROOT) + " a/3 Road", CommandException.class);
    }

    /**
     * Checks optional notes in nonfinal argument positions and persistence after updates and clearing.
     *
     * @throws Exception If a supported command or storage round trip unexpectedly fails.
     */
    @Test
    public void execute_unicodeNote_omitsNormalizesAndClearsBeforeSaving() throws Exception {
        logic.execute("add r/parent n/Alpha Parent o/\u2003 p/91000001 e/alpha@example.com a/1 Test Road");
        assertTrue(model.getAddressBook().getPersonList().getFirst().getNote().isEmpty());
        logic.execute("edit 1 o/\u2003After\u20036 pm\u3000 p/91000001");
        assertEquals("After\u20036 pm", model.getAddressBook().getPersonList().getFirst()
                .getNote().orElseThrow().value);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        logic.execute("edit 1 o/ \t\u2002\u3000 a/1 Test Road");
        Person cleared = model.getAddressBook().getPersonList().getFirst();
        assertTrue(cleared.getNote().isEmpty());
        assertEquals("Alpha Parent", cleared.getName().fullName);
        assertEquals("91000001", cleared.getPhone().value);
        assertEquals("alpha@example.com", cleared.getEmail().value);
        assertEquals("1 Test Road", cleared.getAddress().value);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    /**
     * Checks view composition, insertion-order restoration, and unchanged JSON for padded read-only commands.
     *
     * @throws Exception If a supported command or storage operation unexpectedly fails.
     */
    @Test
    public void execute_unicodePaddedListAndSort_preservesStoredOrderAndRejectsExtraArguments() throws Exception {
        logic.execute(PARENT);
        logic.execute(STUDENT);
        List<Person> insertionOrder = List.copyOf(model.getAddressBook().getPersonList());
        byte[] saved = Files.readAllBytes(storage.getAddressBookFilePath());
        logic.execute("filter n/Alpha");
        Predicate<Person> predicate = model.getFilteredPersonPredicate();
        assertEquals("Contacts sorted by academic level.",
                logic.execute("\u2003sort \u2002LEVEL\u3000").getFeedbackToUser());
        assertFilterMatches(predicate);
        assertEquals("Alpha Student", model.getFilteredPersonList().getFirst().getName().fullName);
        assertEquals("Showing contact: Alpha Student.", logic.execute("view 1").getFeedbackToUser());
        assertRejectedWithoutChanges("sort \u2003level extra\u3000", ParseException.class);
        assertRejectedWithoutChanges("list \u00a0", ParseException.class);
        assertEquals("Showing 2 contacts.", logic.execute("\u2003list\u3000").getFeedbackToUser());
        assertEquals(insertionOrder, List.copyOf(model.getFilteredPersonList()));
        assertArrayEquals(saved, Files.readAllBytes(storage.getAddressBookFilePath()));
    }

    /**
     * Checks that a previously saved blank note loads without losing contacts or parent-student links.
     *
     * @throws Exception If compatible saved data or its round trip cannot be processed.
     */
    @Test
    public void read_legacyWhitespaceNote_preservesContactsLinksAndFileUntilSave() throws Exception {
        logic.execute(PARENT);
        logic.execute(STUDENT);
        logic.execute("link 1 2");
        AddressBook expected = new AddressBook(model.getAddressBook());
        Person parent = expected.getPersonList().getFirst();
        expected.setPerson(parent, new PersonBuilder(parent).withoutNote().build());
        Path path = storage.getAddressBookFilePath();
        String legacyData = Files.readString(path).replace("Original note", "\u2003");
        Files.writeString(path, legacyData);

        ReadOnlyAddressBook loaded = storage.readAddressBook().orElseThrow();
        assertEquals(expected, loaded);
        assertEquals(1, loaded.getLinkList().size());
        assertEquals(legacyData, Files.readString(path));
        storage.saveAddressBook(loaded);
        assertEquals(expected, storage.readAddressBook().orElseThrow());
        assertFalse(Files.readString(path).contains("\u2003"));
    }

    /**
     * Checks rejected input against snapshots of contact data, the view, its state, and saved bytes.
     *
     * @param <T> Type of the rejection.
     * @param command Command expected to be rejected.
     * @param expectedType Expected checked exception type.
     * @return The exception for further feedback assertions.
     * @throws IOException If the saved fixture cannot be read.
     */
    private <T extends Exception> T assertRejectedWithoutChanges(String command, Class<T> expectedType)
            throws IOException {
        AddressBook previous = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        Predicate<Person> predicate = model.getFilteredPersonPredicate();
        Comparator<? super Person> comparator = model.getPersonListComparator();
        byte[] saved = Files.readAllBytes(storage.getAddressBookFilePath());
        T exception = assertThrows(expectedType, () -> logic.execute(command));
        assertEquals(previous, model.getAddressBook());
        assertEquals(displayed, List.copyOf(model.getFilteredPersonList()));
        assertFilterMatches(predicate);
        assertSame(comparator, model.getPersonListComparator());
        assertArrayEquals(saved, Files.readAllBytes(storage.getAddressBookFilePath()));
        return exception;
    }

    /**
     * Checks filter behavior because the model returns a fresh predicate wrapper on each call.
     *
     * @param expected Predicate captured before the command.
     */
    private void assertFilterMatches(Predicate<Person> expected) {
        Predicate<Person> actual = model.getFilteredPersonPredicate();
        for (Person person : model.getAddressBook().getPersonList()) {
            assertEquals(expected.test(person), actual.test(person));
        }
        Person outsideFilter = new PersonBuilder().withName("Outside Filter").build();
        assertEquals(expected.test(outsideFilter), actual.test(outsideFilter));
    }
}
