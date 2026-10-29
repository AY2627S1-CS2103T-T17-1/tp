package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ROLE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests command execution and storage error handling.
 */
public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;
    private CountingJsonAddressBookStorage addressBookStorage;

    @BeforeEach
    public void setUp() {
        addressBookStorage = new CountingJsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, "No contact exists at index 9 in the current list.");
    }

    @Test
    public void execute_invalidDelete_preservesModelAndSavedData() throws Exception {
        model.addPerson(AMY);
        model.updateFilteredPersonList(person -> false);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> false);

        Path dataPath = temporaryFolder.resolve("addressBook.json");
        new JsonAddressBookStorage(dataPath).saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(dataPath);

        assertCommandFailure("delete 1 extra", ParseException.class,
                "Invalid command format. Expected: delete INDEX", expectedModel);
        assertCommandFailure("delete 0", ParseException.class,
                "Index must be a positive integer shown in the current list.", expectedModel);
        assertCommandFailure("delete 1", CommandException.class,
                "No contact exists at index 1 in the current list.", expectedModel);
        assertCommandFailure("delete 2147483648", ParseException.class,
                "No contact exists at index 2147483648 in the current list.", expectedModel);

        assertEquals(savedData, Files.readString(dataPath));
    }

    @Test
    public void execute_invalidPrefixes_preservesModelAndSavedData() throws Exception {
        model.addPerson(AMY);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Path dataPath = temporaryFolder.resolve("addressBook.json");
        new JsonAddressBookStorage(dataPath).saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(dataPath);

        assertCommandFailure("add n/Alex x/foo", ParseException.class, "Unknown prefix: x/.", expectedModel);
        assertCommandFailure("add n/Alex N/Bob", ParseException.class,
                "Prefix n/ may be specified only once.", expectedModel);
        assertEquals(savedData, Files.readString(dataPath));
        assertEquals(0, addressBookStorage.saveCount);
    }

    @Test
    public void execute_validDelete_updatesSavedData() throws Exception {
        model.addPerson(AMY);
        Path dataPath = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(dataPath);
        addressBookStorage.saveAddressBook(model.getAddressBook());

        assertCommandSuccess("delete 1", "Deleted contact: student Amy Bee (11111111).", new ModelManager());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, "No contacts to display.", model);
    }

    @Test
    public void execute_listWithArguments_preservesModelAndSavedData() throws Exception {
        model.addPerson(AMY);
        model.updateFilteredPersonList(person -> false);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> false);

        Path dataPath = temporaryFolder.resolve("addressBook.json");
        new JsonAddressBookStorage(dataPath).saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(dataPath);

        assertCommandFailure("list 3", ParseException.class,
                "Invalid command format. Expected: list", expectedModel);
        assertEquals(savedData, Files.readString(dataPath));
    }

    /**
     * Checks that read-only commands leave storage untouched before and after a data change.
     */
    @Test
    public void execute_readOnlyCommands_doesNotSaveAddressBook() throws Exception {
        String[] readOnlyCommands = {"list", "find Amy", "sort level", "help", "exit"};

        for (String command : readOnlyCommands) {
            logic.execute(command);
        }
        assertEquals(0, addressBookStorage.saveCount);
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));

        logic.execute(AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " s/Mathematics l/Secondary 3");
        assertEquals(1, addressBookStorage.saveCount);

        for (String command : readOnlyCommands) {
            logic.execute(command);
        }
        assertEquals(1, addressBookStorage.saveCount);
    }

    @Test
    public void execute_viewCommand_doesNotSaveAddressBook() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " s/Mathematics l/Secondary 3");
        assertEquals(1, addressBookStorage.saveCount);

        logic.execute("view 1");
        assertEquals(1, addressBookStorage.saveCount);
    }

    @Test
    public void execute_dataChangingCommands_savesAddressBook() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " s/Mathematics l/Secondary 3";

        logic.execute(addCommand);
        assertEquals(1, addressBookStorage.saveCount);

        logic.execute("edit 1 p/22222222");
        assertEquals(2, addressBookStorage.saveCount);

        logic.execute("delete 1");
        assertEquals(3, addressBookStorage.saveCount);

        logic.execute(addCommand);
        logic.execute("clear");
        assertEquals(5, addressBookStorage.saveCount);
    }

    @Test
    public void execute_failedAdd_rollsBackAndExitDoesNotSave() throws Exception {
        addressBookStorage.failNextSave = true;
        String addCommand = AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " s/Mathematics l/Secondary 3";

        assertThrows(CommandException.class, LogicManager.MESSAGE_ADD_SAVE_FAILURE, () -> logic.execute(addCommand));
        assertEquals(new AddressBook(), model.getAddressBook());
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));

        logic.execute("list");
        assertEquals(0, addressBookStorage.saveCount);

        CommandResult result = logic.execute("exit");
        assertTrue(result.isExit());
        assertEquals(0, addressBookStorage.saveCount);
    }

    @Test
    public void execute_failedDelete_preservesDisplayedAndSavedData() throws Exception {
        model.addPerson(AMY);
        model.updateFilteredPersonList(person -> person.equals(AMY));
        addressBookStorage.saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(addressBookStorage.getAddressBookFilePath());
        addressBookStorage.failNextSave = true;

        assertThrows(CommandException.class,
                LogicManager.MESSAGE_DELETE_SAVE_FAILURE, () -> logic.execute("delete 1"));

        assertEquals(AMY, model.getFilteredPersonList().get(0));
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(savedData, Files.readString(addressBookStorage.getAddressBookFilePath()));
        logic.execute("exit");
        assertEquals(1, addressBookStorage.saveCount);
    }

    @Test
    public void execute_failedAdd_preservesFilteredListAndSavedData() throws Exception {
        model.addPerson(AMY);
        model.updateFilteredPersonList(person -> false);
        addressBookStorage.saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(addressBookStorage.getAddressBookFilePath());
        addressBookStorage.failNextSave = true;

        String addCommand = "add r/student n/Bob Builder p/99999999 e/bob@example.com a/Street"
                + " s/Mathematics l/Secondary 3";
        assertThrows(CommandException.class,
                LogicManager.MESSAGE_ADD_SAVE_FAILURE, () -> logic.execute(addCommand));

        assertEquals(0, model.getFilteredPersonList().size());
        assertEquals(new AddressBook(addressBookStorage.readAddressBook().orElseThrow()), model.getAddressBook());
        assertEquals(savedData, Files.readString(addressBookStorage.getAddressBookFilePath()));
    }

    @Test
    public void execute_invalidDataFile_rejectsChangesWithoutOverwriting() throws Exception {
        Path dataPath = addressBookStorage.getAddressBookFilePath();
        Files.writeString(dataPath, "invalid data");
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        logic = new LogicManager(model, storage, false);

        String addCommand = AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " s/Mathematics l/Secondary 3";
        assertThrows(CommandException.class,
                LogicManager.MESSAGE_ADD_SAVE_FAILURE, () -> logic.execute(addCommand));
        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals("invalid data", Files.readString(dataPath));
        assertEquals(0, addressBookStorage.saveCount);
        logic.execute("sort level");
        assertEquals(0, addressBookStorage.saveCount);
        assertEquals("invalid data", Files.readString(dataPath));
        assertTrue(logic.execute("exit").isExit());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, LogicManager.MESSAGE_ADD_SAVE_FAILURE);
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        model.addPerson(AMY);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Path dataPath = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(dataPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_AD_EXCEPTION;
            }
        };
        StorageManager storage = new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        logic = new LogicManager(model, storage);

        // The message explains what was not done, without the storage exception's file path.
        assertCommandFailure("clear", CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE,
                expectedModel);
    }

    @Test
    public void execute_linkSaveFails_reportsGeneralMessageAndKeepsContactsUnlinked() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(GEORGE);
        addressBookStorage.failNextSave = true;

        assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () -> logic.execute("link 2 1"));

        assertFalse(model.hasLink(ParentStudentLink.between(GEORGE, ALICE)));
        assertEquals(List.of(), model.getLinkedPersons(GEORGE));
    }

    @Test
    public void execute_editSaveThrowsIoException_restoresModelAndReportsError() throws Exception {
        model.addPerson(AMY);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        addressBookStorage.failNextSave = true;

        assertCommandFailure("edit 1 p/22222222", CommandException.class, LogicManager.MESSAGE_EDIT_SAVE_FAILURE,
                expectedModel);
        assertEquals(AMY, model.getFilteredPersonList().get(0));
    }

    /**
     * Checks that role-specific student edits and note clearing are saved and can be reloaded.
     */
    @Test
    public void execute_editRoleSpecificFields_persistsThroughReload() throws Exception {
        Person student = new PersonBuilder().withNote("Old note").build();
        model.addPerson(student);
        addressBookStorage.saveAddressBook(model.getAddressBook());

        logic.execute("edit 1 s/Physics l/JC 1 o/New note");

        Person expected = new PersonBuilder(student).withSubject("Physics").withLevel("JC 1")
                .withNote("New note").build();
        assertEquals(expected, model.getAddressBook().getPersonList().get(0));
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
        assertEquals(2, addressBookStorage.saveCount);

        logic.execute("edit 1 o/");
        Person withoutNote = new PersonBuilder(expected).withoutNote().build();
        assertEquals(withoutNote, addressBookStorage.readAddressBook().orElseThrow().getPersonList().get(0));
        assertEquals(3, addressBookStorage.saveCount);
    }

    /**
     * Checks that student-only fields on a parent are rejected without changing displayed or saved data.
     */
    @Test
    public void execute_editParentWithStudentField_preservesDisplayedAndSavedData() throws Exception {
        Person parent = new PersonBuilder().withRole("parent").withNote("Original note").build();
        model.addPerson(parent);
        model.updateFilteredPersonList(person -> person.getRole() == parent.getRole());
        addressBookStorage.saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(addressBookStorage.getAddressBookFilePath());

        assertThrows(CommandException.class, AddCommand.MESSAGE_PARENT_FIELDS, () -> logic.execute(
                "edit 1 s/Math o/Changed note"));

        assertEquals(parent, model.getFilteredPersonList().get(0));
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(savedData, Files.readString(addressBookStorage.getAddressBookFilePath()));
        assertEquals(1, addressBookStorage.saveCount);
    }

    /**
     * Checks that a failed role-specific edit save restores the filtered list and leaves saved data unchanged.
     */
    @Test
    public void execute_editRoleSpecificSaveFailure_restoresFilteredListAndSavedData() throws Exception {
        Person student = new PersonBuilder().withNote("Original note").build();
        Person parent = new PersonBuilder().withName("Bob Builder").withRole("parent").build();
        model.addPerson(student);
        model.addPerson(parent);
        model.updateFilteredPersonList(person -> person.getSubject().isPresent());
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(student, model.getFilteredPersonList().get(0));
        addressBookStorage.saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(addressBookStorage.getAddressBookFilePath());
        addressBookStorage.failNextSave = true;

        assertThrows(CommandException.class, LogicManager.MESSAGE_EDIT_SAVE_FAILURE, () ->
                logic.execute("edit 1 s/Physics o/"));

        assertEquals(student, model.getFilteredPersonList().get(0));
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(parent, model.getAddressBook().getPersonList().get(1));
        assertEquals(savedData, Files.readString(addressBookStorage.getAddressBookFilePath()));
        assertEquals(1, addressBookStorage.saveCount);
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " s/Mathematics l/Secondary 3";
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, new ModelManager());
    }

    /**
     * Counts address book saves while retaining real file storage behavior.
     */
    private static class CountingJsonAddressBookStorage extends JsonAddressBookStorage {
        private int saveCount;
        private boolean failNextSave;

        CountingJsonAddressBookStorage(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            if (failNextSave) {
                failNextSave = false;
                throw new IOException("temporary save error");
            }

            super.saveAddressBook(addressBook);
            saveCount++;
        }
    }

    /**
     * Checks that invalid input and failed mutations preserve the sorted view, filter, and saved data.
     */
    @Test
    public void execute_failedCommands_preservesSortedFilterAndSavedData() throws Exception {
        Person later = new PersonBuilder().withName("Zoe").withLevel("JC 2").build();
        Person earlier = new PersonBuilder().withName("Amy").withLevel("Primary 1").build();
        Person hidden = new PersonBuilder().withRole("parent").withName("Parent").build();
        model.addPerson(later);
        model.addPerson(hidden);
        model.addPerson(earlier);
        model.updateFilteredPersonList(person -> person.getLevel().isPresent());
        addressBookStorage.saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(addressBookStorage.getAddressBookFilePath());
        logic.execute("sort level");

        assertThrows(ParseException.class, "Invalid command format. Expected: sort level", () -> {
            logic.execute("sort name");
        });
        assertEquals(List.of(earlier, later), model.getFilteredPersonList());
        addressBookStorage.failNextSave = true;

        assertThrows(CommandException.class, LogicManager.MESSAGE_DELETE_SAVE_FAILURE, () -> {
            logic.execute("delete 1");
        });
        assertEquals(List.of(earlier, later), model.getFilteredPersonList());

        addressBookStorage.failNextSave = true;
        assertThrows(CommandException.class, LogicManager.MESSAGE_EDIT_SAVE_FAILURE, () -> {
            logic.execute("edit 1 n/Zed");
        });
        assertEquals(List.of(earlier, later), model.getFilteredPersonList());

        addressBookStorage.failNextSave = true;
        assertThrows(CommandException.class, LogicManager.MESSAGE_ADD_SAVE_FAILURE, () -> {
            logic.execute("add r/student n/Bob p/99999999 e/bob@example.com a/Street"
                    + " s/Mathematics l/Primary 2");
        });

        assertEquals(List.of(earlier, later), model.getFilteredPersonList());
        assertFalse(model.getFilteredPersonPredicate().test(hidden));
        assertEquals(List.of(later, hidden, earlier), model.getAddressBook().getPersonList());
        assertEquals(savedData, Files.readString(addressBookStorage.getAddressBookFilePath()));
        assertEquals(1, addressBookStorage.saveCount);
    }
}
