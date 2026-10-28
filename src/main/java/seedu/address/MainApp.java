package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.3";
    public static final String DATA_FILE_WARNING = "Warning: TutorContacts could not load data/tutorcontacts.json. "
            + "The existing file was not changed.";
    public static final String PREFS_FILE_WARNING = "Warning: TutorContacts could not load preferences.json. "
            + "Default window settings are in use; the existing file was not changed.";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = Paths.get("data", "tutorcontacts.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    private boolean dataFileLoadFailed;
    // Keep an unreadable preferences file intact at startup and shutdown.
    private boolean prefsFileLoadFailed;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing TutorContacts ]==========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(ADDRESS_BOOK_FILE_PATH);
        storage = new StorageManager(addressBookStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage, !dataFileLoadFailed);

        ui = new UiManager(logic, storage.getAddressBookFilePath(), getStartupWarning());
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * Starts with an empty address book if the data file is missing or cannot be loaded.
     * Records a warning and prevents writes when an existing file cannot be loaded.
     *
     * @param storage Storage used to load contact data.
     * @param userPrefs Preferences to use in the model.
     * @return A model containing saved contacts or an empty contact list.
     */
    Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        dataFileLoadFailed = false;
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Data file is missing. Starting with an empty contact list.");
            }
            initialData = addressBookOptional.orElseGet(AddressBook::new);
        } catch (DataLoadingException e) {
            dataFileLoadFailed = true;
            logger.warning(DATA_FILE_WARNING);
            initialData = new AddressBook();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns warnings for files that could not be loaded, or {@code null} if both files loaded successfully.
     *
     * @return The warning to display after startup, or {@code null} if loading succeeded.
     */
    String getStartupWarning() {
        if (dataFileLoadFailed && prefsFileLoadFailed) {
            return DATA_FILE_WARNING + "\n" + PREFS_FILE_WARNING;
        }
        if (dataFileLoadFailed) {
            return DATA_FILE_WARNING;
        }
        return prefsFileLoadFailed ? PREFS_FILE_WARNING : null;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or default settings if the file is missing or cannot be read. An unreadable file is left unchanged.
     *
     * @param storage Storage used to read and save window preferences.
     * @return The stored preferences or default settings when they cannot be loaded.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        prefsFileLoadFailed = false;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            prefsFileLoadFailed = true;
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences without changing the file.");
            initializedPrefs = new UserPrefs();
        }

        // Create a missing file or update valid preferences, but never replace a file that failed to load.
        if (!prefsFileLoadFailed) {
            try {
                storage.saveUserPrefs(initializedPrefs);
            } catch (IOException e) {
                logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
            }
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {

        logger.info("Starting TutorContacts " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping TutorContacts ] =============================");
        if (prefsFileLoadFailed) {
            return;
        }
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
