package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";
    public static final String MESSAGE_ADD_SAVE_FAILURE =
            "Contact was not added because TutorContacts could not save the data file.";
    public static final String MESSAGE_DELETE_SAVE_FAILURE =
            "Contact was not deleted because TutorContacts could not save the data file.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private final boolean isDataFileWritable;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this(model, storage, true);
    }

    /**
     * Constructs a logic manager that rejects data changes if the existing data file could not be loaded.
     *
     * @param model Model to execute commands against.
     * @param storage Storage used to save successful changes.
     * @param isDataFileWritable Whether the existing data file may safely be replaced.
     */
    public LogicManager(Model model, Storage storage, boolean isDataFileWritable) {
        this.model = model;
        this.storage = storage;
        this.isDataFileWritable = isDataFileWritable;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);
        if (command.isDataChanging() && !isDataFileWritable) {
            IOException cause = new IOException("Data file could not be loaded.");
            throw new CommandException(getSaveFailureMessage(command, cause), cause);
        }

        if (!command.isDataChanging()) {
            return command.execute(model);
        }

        AddressBook previousAddressBook = new AddressBook(model.getAddressBook());
        Predicate<Person> previousFilter = model.getFilteredPersonPredicate();
        CommandResult commandResult = command.execute(model);

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (IOException e) {
            model.setAddressBook(previousAddressBook);
            model.updateFilteredPersonList(previousFilter);
            throw new CommandException(getSaveFailureMessage(command, e), e);
        }

        return commandResult;
    }

    /**
     * Returns the specified feedback for a failed add or delete save, or a storage error for other commands.
     *
     * @param command Command whose data could not be saved.
     * @param exception Cause of the save failure.
     * @return The feedback to show for the failed command.
     */
    private static String getSaveFailureMessage(Command command, IOException exception) {
        if (command instanceof AddCommand) {
            return MESSAGE_ADD_SAVE_FAILURE;
        }
        if (command instanceof DeleteCommand) {
            return MESSAGE_DELETE_SAVE_FAILURE;
        }
        if (exception instanceof AccessDeniedException) {
            return String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, exception.getMessage());
        }
        return String.format(FILE_OPS_ERROR_FORMAT, exception.getMessage());
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
