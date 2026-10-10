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
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public void execute_validDelete_updatesSavedData() throws Exception {
        model.addPerson(AMY);
        Path dataPath = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(dataPath);
        addressBookStorage.saveAddressBook(model.getAddressBook());

        assertCommandSuccess("delete 1", "Deleted contact: Amy Bee.", new ModelManager());
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

    @Test
    public void execute_readOnlyCommands_doesNotSaveAddressBook() throws Exception {
        String[] readOnlyCommands = {"list", "find Amy", "help", "exit"};

        for (String command : readOnlyCommands) {
            logic.execute(command);
        }
        assertEquals(0, addressBookStorage.saveCount);
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));

        logic.execute(AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        assertEquals(1, addressBookStorage.saveCount);

        for (String command : readOnlyCommands) {
            logic.execute(command);
        }
        assertEquals(1, addressBookStorage.saveCount);
    }

    @Test
    public void execute_dataChangingCommands_savesAddressBook() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;

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
    public void execute_exitAfterFailedSave_retriesUnsavedChanges() throws Exception {
        addressBookStorage.failNextSave = true;
        String addCommand = AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        String expectedMessage = String.format(LogicManager.FILE_OPS_ERROR_FORMAT, "temporary save error");

        assertThrows(CommandException.class, expectedMessage, () -> logic.execute(addCommand));
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));

        logic.execute("list");
        assertEquals(0, addressBookStorage.saveCount);

        CommandResult result = logic.execute("exit");
        assertTrue(result.isExit());
        assertEquals(1, addressBookStorage.saveCount);
        AddressBook savedAddressBook = new AddressBook(addressBookStorage.readAddressBook().get());
        assertEquals(model.getAddressBook(), savedAddressBook);
    }

    @Test
    public void execute_exitWhenRetryFails_throwsCommandException() {
        addressBookStorage.failNextSave = true;
        String addCommand = AddCommand.COMMAND_WORD + ROLE_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        String expectedMessage = String.format(LogicManager.FILE_OPS_ERROR_FORMAT, "temporary save error");

        assertThrows(CommandException.class, expectedMessage, () -> logic.execute(addCommand));

        addressBookStorage.failNextSave = true;
        assertThrows(CommandException.class, expectedMessage, () -> logic.execute("exit"));
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
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
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
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
}
