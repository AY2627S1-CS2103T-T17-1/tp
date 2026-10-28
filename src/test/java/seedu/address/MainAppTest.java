package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

/**
 * Tests application configuration that can be checked without starting JavaFX.
 */
public class MainAppTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void addressBookFilePath_usesTutorContactsJson() throws ReflectiveOperationException {
        Field filePathField = MainApp.class.getDeclaredField("ADDRESS_BOOK_FILE_PATH");
        filePathField.setAccessible(true);

        assertEquals(Paths.get("data", "tutorcontacts.json"), filePathField.get(null));
    }

    @Test
    public void init_wiresApplicationComponentsWithoutWritingFiles() throws Exception {
        MainApp app = new MainApp() {
            @Override
            protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
                return new UserPrefs();
            }

            @Override
            Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
                return new ModelManager(new AddressBook(), userPrefs);
            }
        };

        app.init();

        assertNotNull(app.storage);
        assertNotNull(app.model);
        assertNotNull(app.logic);
        assertNotNull(app.ui);
        assertNull(app.getStartupWarning());
    }

    @Test
    public void initModelManager_missingFile_startsEmptyWithoutWarning() {
        Path dataPath = temporaryFolder.resolve("tutorcontacts.json");
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataPath), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertNull(app.getStartupWarning());
        assertFalse(Files.exists(dataPath));
    }

    @Test
    public void initModelManager_invalidFile_warnsAndPreservesFile() throws Exception {
        Path dataPath = temporaryFolder.resolve("tutorcontacts.json");
        String invalidData = "not valid JSON";
        Files.writeString(dataPath, invalidData);
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataPath), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(MainApp.DATA_FILE_WARNING, app.getStartupWarning());
        assertEquals(invalidData, Files.readString(dataPath));
    }

    /**
     * Checks startup recovery, write protection, and successful loading after repair and restart.
     */
    @Test
    public void init_invalidJson_warnsBlocksChangesAndRecoversAfterRestart() throws Exception {
        String validData = "{\"persons\":[{\"role\":\"parent\",\"name\":\"Alpha Parent\",\"phone\":\"91000001\","
                + "\"email\":\"alpha@example.com\",\"address\":\"1 Test Road\"}]}";
        String[] invalidDocuments = {"null", validData + " unexpected trailing content",
            validData + " {}", validData + " null"};
        for (String json : invalidDocuments) {
            assertInvalidDocumentRecovery(json);
        }
    }

    /**
     * Checks the complete recovery workflow for one invalid contact document.
     */
    private void assertInvalidDocumentRecovery(String json) throws Exception {
        Path dataPath = temporaryFolder.resolve("tutorcontacts.json");
        Files.writeString(dataPath, json);
        byte[] originalData = Files.readAllBytes(dataPath);
        MainApp app = createApp(dataPath);

        app.init();

        assertEquals(new AddressBook(), app.model.getAddressBook());
        assertEquals(MainApp.DATA_FILE_WARNING, app.getStartupWarning());
        assertEquals(ListCommand.MESSAGE_EMPTY_LIST, app.logic.execute("list").getFeedbackToUser());
        String addCommand = "add r/parent n/Alpha Parent p/91000001 e/alpha@example.com a/1 Test Road";
        assertThrows(CommandException.class,
                LogicManager.MESSAGE_ADD_SAVE_FAILURE, () -> app.logic.execute(addCommand));
        assertEquals(new AddressBook(), app.model.getAddressBook());
        assertArrayEquals(originalData, Files.readAllBytes(dataPath));

        // Repairing the file does not remove protection until the app is restarted.
        Files.writeString(dataPath, "{\"persons\":[]}");
        byte[] repairedData = Files.readAllBytes(dataPath);
        assertThrows(CommandException.class, () -> app.logic.execute(addCommand));
        assertArrayEquals(repairedData, Files.readAllBytes(dataPath));

        MainApp restartedApp = createApp(dataPath);
        restartedApp.init();

        assertNull(restartedApp.getStartupWarning());
        restartedApp.logic.execute(addCommand);
        assertEquals(1, restartedApp.model.getAddressBook().getPersonList().size());
        assertEquals(restartedApp.model.getAddressBook(), createStorage(dataPath).readAddressBook().orElseThrow());
    }

    /**
     * Checks that the shared JSON fix lets malformed preferences recover to the existing defaults.
     */
    @Test
    public void initPrefs_invalidJson_usesDefaultPreferences() throws Exception {
        Path prefsPath = temporaryFolder.resolve("preferences.json");
        MainApp app = new MainApp();
        for (String json : new String[]{"null", "{} trailing text", "{} {}"}) {
            Files.writeString(prefsPath, json);

            UserPrefs prefs = app.initPrefs(new JsonUserPrefsStorage(prefsPath));

            assertEquals(new UserPrefs(), prefs);
            assertEquals(prefs, JsonUtil.readJsonFile(prefsPath, UserPrefs.class).orElseThrow());
        }
    }

    @Test
    public void initModelManager_nullContact_warnsAndPreservesFile() throws Exception {
        Path dataPath = temporaryFolder.resolve("tutorcontacts.json");
        String invalidData = "{\"persons\":[null]}";
        Files.writeString(dataPath, invalidData);
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataPath), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(MainApp.DATA_FILE_WARNING, app.getStartupWarning());
        assertEquals(invalidData, Files.readString(dataPath));
    }

    @Test
    public void initModelManager_duplicateContact_warnsAndPreservesFile() throws Exception {
        Path dataPath = temporaryFolder.resolve("tutorcontacts.json");
        Path duplicateData = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest",
                "duplicatePersonAddressBook.json");
        Files.copy(duplicateData, dataPath);
        String savedData = Files.readString(dataPath);
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataPath), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(MainApp.DATA_FILE_WARNING, app.getStartupWarning());
        assertEquals(savedData, Files.readString(dataPath));
    }

    @Test
    public void initModelManager_unreadableDataPath_warnsAndPreservesDirectory() throws Exception {
        Path dataPath = temporaryFolder.resolve("tutorcontacts.json");
        Files.createDirectory(dataPath);
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataPath), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(MainApp.DATA_FILE_WARNING, app.getStartupWarning());
        assertTrue(Files.isDirectory(dataPath));
    }

    /**
     * Creates storage backed by a temporary contact file for startup tests.
     */
    private StorageManager createStorage(Path dataPath) {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(dataPath);
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"));
        return new StorageManager(addressBookStorage, userPrefsStorage);
    }

    /**
     * Creates an application that uses temporary contact storage without writing real preferences.
     */
    private MainApp createApp(Path dataPath) {
        Storage testStorage = createStorage(dataPath);
        return new MainApp() {
            @Override
            protected UserPrefs initPrefs(JsonUserPrefsStorage prefsStorage) {
                return new UserPrefs();
            }

            @Override
            Model initModelManager(Storage initialStorage, ReadOnlyUserPrefs userPrefs) {
                storage = testStorage;
                return super.initModelManager(testStorage, userPrefs);
            }
        };
    }
}
