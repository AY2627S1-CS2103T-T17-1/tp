package seedu.address.logic;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
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
    public static final String MESSAGE_ADD_SAVE_FAILURE =
            "Contact was not added because TutorContacts could not save the data file.";
    public static final String MESSAGE_DELETE_SAVE_FAILURE =
            "Contact was not deleted because TutorContacts could not save the data file.";
    public static final String MESSAGE_EDIT_SAVE_FAILURE =
            "Contact was not edited because TutorContacts could not save the data file.";
    public static final String MESSAGE_SAVE_FAILURE =
            "The change was not made because TutorContacts could not save the data file.";

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
            throw new CommandException(getSaveFailureMessage(command), cause);
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
            // The cause, which may name an internal temporary file, is logged rather than shown to the user.
            logger.warning("Could not save the data file, so the command was rolled back: " + e);
            throw new CommandException(getSaveFailureMessage(command), e);
        }

        return commandResult;
    }

    /**
     * Returns the feedback for a command whose changes could not be saved.
     * The common contact commands say what was not done; other commands, such as {@code link} and {@code clear},
     * share a general message. Storage details, which may name an internal temporary file, are left to the log.
     *
     * @param command Command whose data could not be saved.
     * @return The feedback to show for the failed command.
     */
    private static String getSaveFailureMessage(Command command) {
        if (command instanceof AddCommand) {
            return MESSAGE_ADD_SAVE_FAILURE;
        }
        if (command instanceof DeleteCommand) {
            return MESSAGE_DELETE_SAVE_FAILURE;
        }
        if (command instanceof EditCommand) {
            return MESSAGE_EDIT_SAVE_FAILURE;
        }
        return MESSAGE_SAVE_FAILURE;
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public List<Person> getLinkedPersons(Person person) {
        return model.getLinkedPersons(person);
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
