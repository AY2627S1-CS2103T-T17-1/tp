package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Exercises all five MVP commands through the real parser, model, and JSON persistence.
 */
public class MvpCommandIntegrationTest {
    private static final String DETAILS = " n/Alex Tan p/91234567 e/alex@example.com a/1 Road";
    private static final String STUDENT = "add r/student" + DETAILS + " s/A-Math l/Secondary 3 o/Discuss \\x/foo";

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_allFiveCommands_preservesFieldsAndPresentation() throws Exception {
        CommandResult addResult = logic.execute(STUDENT);
        Person student = model.getFilteredPersonList().getFirst();
        assertEquals("Added student: Alex Tan.", addResult.getFeedbackToUser());
        assertEquals(CommandResult.ContactListAction.SELECT, addResult.getContactListAction());
        assertEquals(student, addResult.getContact().orElseThrow());
        assertEquals("A-Math", student.getSubject().orElseThrow().value);
        assertEquals("Secondary 3", student.getLevel().orElseThrow().value);
        assertEquals("Discuss x/foo", student.getNote().orElseThrow().value);

        // Family members may share their name and contact details across roles.
        logic.execute("add r/parent" + DETAILS + " o/");
        Person parent = model.getFilteredPersonList().get(1);
        assertTrue(parent.getSubject().isEmpty());
        assertTrue(parent.getLevel().isEmpty());
        assertTrue(parent.getNote().isEmpty());
        String savedData = Files.readString(storage.getAddressBookFilePath());

        assertEquals("Showing 2 contacts.", logic.execute("LIST").getFeedbackToUser());
        CommandResult viewResult = logic.execute("view 1");
        assertEquals("Showing contact: Alex Tan.", viewResult.getFeedbackToUser());
        assertEquals(CommandResult.ContactListAction.SELECT, viewResult.getContactListAction());
        assertTrue(logic.execute("ExIt").isExit());
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));

        Model restored = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(model.getAddressBook(), restored.getAddressBook());
        assertEquals("Deleted contact: parent Alex Tan (91234567).", logic.execute("delete 2").getFeedbackToUser());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        logic.execute("delete 1");
        assertEquals("No contacts to display.", logic.execute("list").getFeedbackToUser());
    }

    @Test
    public void execute_invalidInputs_preservesModelAndSavedFile() throws Exception {
        logic.execute(STUDENT);
        AddressBook before = new AddressBook(model.getAddressBook());
        String savedData = Files.readString(storage.getAddressBookFilePath());
        String[] invalidCommands = {"add r/student", "add r/student" + DETAILS,
            "add r/parent" + DETAILS + " s/Math", STUDENT + " t/friend", STUDENT + " O/Again",
            "exit 3", "list extra", "view 0", "view 99", "delete 1 extra", "delete 99"};
        for (String input : invalidCommands) {
            try {
                logic.execute(input);
                throw new AssertionError("Invalid command succeeded: " + input);
            } catch (ParseException | CommandException expected) {
                assertEquals(before, model.getAddressBook(), input);
                assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()), input);
            }
        }
        assertThrows(ParseException.class, ExitCommand.MESSAGE_INVALID_ARGUMENTS, () -> logic.execute("exit 3"));
    }

    @Test
    public void execute_duplicates_useCaseInsensitiveNameAndPhoneOrEmail() throws Exception {
        logic.execute(STUDENT);
        String duplicatePhone = "add r/STUDENT n/alex   tan p/+9123-4567 e/other@example.com a/2 Road"
                + " s/Physics l/JC 1";
        assertThrows(CommandException.class, "This contact duplicates an existing student: alex tan.", () -> {
            logic.execute(duplicatePhone);
        });
        String duplicateEmail = "add r/student n/ALEX TAN p/88888888 e/ALEX@EXAMPLE.COM a/2 Road"
                + " s/Physics l/JC 1";
        assertThrows(CommandException.class, "This contact duplicates an existing student: ALEX TAN.", () -> {
            logic.execute(duplicateEmail);
        });
        assertEquals(1, model.getFilteredPersonList().size());
        logic.execute("add r/student n/Alex Tan p/88888888 e/other@example.com a/2 Road s/Physics l/JC 1");
        assertEquals(2, model.getFilteredPersonList().size());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_missingFields_reportsExactFormat() {
        assertThrows(ParseException.class, AddCommand.MESSAGE_INVALID_FORMAT, () -> logic.execute("add"));
    }
}
