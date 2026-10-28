package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.util.AppPaths;
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

    /**
     * Checks that contact storage is resolved beneath the shared application home directory.
     */
    @Test
    public void addressBookFilePath_usesTutorContactsJson() throws ReflectiveOperationException {
        Field filePathField = MainApp.class.getDeclaredField("ADDRESS_BOOK_FILE_PATH");
        filePathField.setAccessible(true);

        Path expectedPath = AppPaths.getHomeDirectory().resolve("data").resolve("tutorcontacts.json");
        assertEquals(expectedPath, filePathField.get(null));
    }

    /**
     * Checks that preferences use the same application home directory as contact data.
     */
    @Test
    public void userPrefsFilePath_usesApplicationHome() throws ReflectiveOperationException {
        Field filePathField = MainApp.class.getDeclaredField("USER_PREFS_FILE_PATH");
        filePathField.setAccessible(true);

        assertEquals(AppPaths.getHomeDirectory().resolve("preferences.json"), filePathField.get(null));
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
    public void initModelManager_nullTag_warnsAndPreservesFile() throws Exception {
        Path dataPath = temporaryFolder.resolve("tutorcontacts.json");
        String invalidData = "{\"persons\":[{\"role\":\"student\",\"name\":\"Alex Tan\",\"phone\":\"91234567\","
                + "\"email\":\"alex@example.com\",\"address\":\"1 Road\",\"tags\":[null]}]}";
        Files.writeString(dataPath, invalidData);
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataPath), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(MainApp.DATA_FILE_WARNING, app.getStartupWarning());
        assertEquals(invalidData, Files.readString(dataPath));
    }

    @Test
    public void initPrefs_missingFile_createsDefaultPreferencesWithoutWarning() throws Exception {
        Path prefsPath = temporaryFolder.resolve("preferences.json");
        MainApp app = new MainApp();

        UserPrefs prefs = app.initPrefs(new JsonUserPrefsStorage(prefsPath));

        assertEquals(new UserPrefs(), prefs);
        assertNull(app.getStartupWarning());
        assertEquals(prefs, new JsonUserPrefsStorage(prefsPath).readUserPrefs().orElseThrow());
    }

    @Test
    public void initPrefs_saveFails_usesDefaultPreferences() {
        Path prefsPath = temporaryFolder.resolve("preferences.json");
        JsonUserPrefsStorage prefsStorage = new JsonUserPrefsStorage(prefsPath) {
            @Override
            public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
                throw new IOException("Test save failure");
            }
        };
        MainApp app = new MainApp();

        assertEquals(new UserPrefs(), app.initPrefs(prefsStorage));
        assertNull(app.getStartupWarning());
        assertFalse(Files.exists(prefsPath));
    }

    @Test
    public void initPrefs_invalidFile_warnsAndPreservesFileOnStartupAndExit() throws Exception {
        Path prefsPath = temporaryFolder.resolve("preferences.json");
        String invalidPrefs = "{invalid-json";
        Files.writeString(prefsPath, invalidPrefs);
        MainApp app = new MainApp();

        UserPrefs prefs = app.initPrefs(new JsonUserPrefsStorage(prefsPath));

        assertEquals(new UserPrefs(), prefs);
        assertEquals(MainApp.PREFS_FILE_WARNING, app.getStartupWarning());
        assertEquals(invalidPrefs, Files.readString(prefsPath));

        app.model = new ModelManager(new AddressBook(), prefs);
        app.storage = createStorage(temporaryFolder.resolve("tutorcontacts.json"));
        app.stop();
        assertEquals(invalidPrefs, Files.readString(prefsPath));
    }

    @Test
    public void initPrefs_validFile_savesUpdatedSettingsOnExit() throws Exception {
        Path prefsPath = temporaryFolder.resolve("preferences.json");
        JsonUserPrefsStorage prefsStorage = new JsonUserPrefsStorage(prefsPath);
        prefsStorage.saveUserPrefs(new UserPrefs());
        MainApp app = new MainApp();

        UserPrefs prefs = app.initPrefs(prefsStorage);
        app.model = new ModelManager(new AddressBook(), prefs);
        app.storage = createStorage(temporaryFolder.resolve("tutorcontacts.json"));
        GuiSettings updatedSettings = new GuiSettings(1000, 600, 20, 30);
        app.model.setGuiSettings(updatedSettings);
        app.stop();

        assertNull(app.getStartupWarning());
        assertEquals(updatedSettings, prefsStorage.readUserPrefs().orElseThrow().getGuiSettings());
    }

    @Test
    public void initPrefsAndModelManager_bothFilesInvalid_displaysBothWarnings() throws Exception {
        Path prefsPath = temporaryFolder.resolve("preferences.json");
        Path dataPath = temporaryFolder.resolve("tutorcontacts.json");
        Files.writeString(prefsPath, "{invalid-json");
        Files.writeString(dataPath, "{invalid-json");
        MainApp app = new MainApp();

        UserPrefs prefs = app.initPrefs(new JsonUserPrefsStorage(prefsPath));
        app.initModelManager(createStorage(dataPath), prefs);

        assertEquals(MainApp.DATA_FILE_WARNING + "\n" + MainApp.PREFS_FILE_WARNING, app.getStartupWarning());
        assertEquals("{invalid-json", Files.readString(prefsPath));
        assertEquals("{invalid-json", Files.readString(dataPath));
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
}
