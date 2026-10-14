package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
