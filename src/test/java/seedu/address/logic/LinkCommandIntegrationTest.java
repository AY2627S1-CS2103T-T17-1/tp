package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Exercises parent-student links through the real parser, model, and JSON persistence.
 */
public class LinkCommandIntegrationTest {
    private static final String SHARED_DETAILS = " n/Alex Tan p/91234567 e/alex@example.com a/1 Road";

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));

        // A parent and student may share a name and phone, so links must also match on role.
        logic.execute("add r/student" + SHARED_DETAILS + " s/Mathematics l/Secondary 3");
        logic.execute("add r/parent" + SHARED_DETAILS);
        logic.execute("add r/student n/Bea Tan p/92345678 e/bea@example.com a/1 Road s/Physics l/JC 1");
    }

    @Test
    public void execute_linkEditRestartDelete_keepsLinksConsistent() throws Exception {
        assertEquals("Linked parent Alex Tan (91234567) to student Alex Tan (91234567).",
                logic.execute("link 2 1").getFeedbackToUser());
        assertEquals("Linked parent Alex Tan (91234567) to student Bea Tan (92345678).",
                logic.execute("link 2 3").getFeedbackToUser());
        Person parent = model.getFilteredPersonList().get(1);
        assertEquals(List.of("Alex Tan", "Bea Tan"), linkedNames(model, parent));

        // Editing a linked student's phone keeps the link.
        logic.execute("edit 3 p/93456789");
        assertEquals(List.of("Alex Tan", "Bea Tan"), linkedNames(model, parent));

        // Links survive a restart.
        Model restored = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(model.getAddressBook(), restored.getAddressBook());

        assertEquals("Deleted contact: parent Alex Tan (91234567). Removed 2 link(s).",
                logic.execute("delete 2").getFeedbackToUser());
        assertEquals(List.of(), storage.readAddressBook().orElseThrow().getLinkList());
    }

    @Test
    public void execute_invalidLink_leavesSavedDataUnchanged() throws Exception {
        logic.execute("link 2 1");

        assertThrows(CommandException.class, "Alex Tan (91234567) is already linked to Alex Tan (91234567).", () ->
                logic.execute("link 2 1"));
        assertThrows(CommandException.class, "The contact at index 1 is not a parent.", () ->
                logic.execute("link 1 3"));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(1, model.getAddressBook().getLinkList().size());
    }

    @Test
    public void execute_studentsWithSameName_linksTheChosenStudent() throws Exception {
        logic.execute("add r/student n/Alex Tan p/98765432 e/alex.other@example.com a/2 Road s/English l/Primary 6");

        assertEquals("Linked parent Alex Tan (91234567) to student Alex Tan (98765432).",
                logic.execute("link 2 4").getFeedbackToUser());
        Person parent = model.getFilteredPersonList().get(1);
        assertEquals(List.of(model.getFilteredPersonList().get(3)), model.getLinkedPersons(parent));
    }

    private static List<String> linkedNames(Model model, Person person) {
        return model.getLinkedPersons(person).stream().map(linked -> linked.getName().fullName).toList();
    }
}
