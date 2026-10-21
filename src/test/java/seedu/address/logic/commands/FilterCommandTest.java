package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.ContactMatchesCriteriaPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests filter execution feedback and the read-only command contract.
 */
public class FilterCommandTest {
    private final ContactMatchesCriteriaPredicate predicate =
            new ContactMatchesCriteriaPredicate(List.of("Alice"), Optional.empty(), Optional.empty());

    /**
     * Checks match-count feedback, default name ordering, and unchanged stored order.
     */
    @Test
    public void execute_zeroOneAndMultipleMatches_reportsCount() {
        Model model = new ModelManager();
        FilterCommand command = new FilterCommand(predicate);
        assertEquals(new CommandResult("No matching contacts."), command.execute(model));

        Person zed = new PersonBuilder().withName("Zed Alice").build();
        Person alice = new PersonBuilder().withName("Alice Tan").build();
        model.addPerson(zed);
        assertEquals(new CommandResult("Showing 1 matching contacts."), command.execute(model));
        model.addPerson(alice);
        assertEquals(new CommandResult("Showing 2 matching contacts."), command.execute(model));
        assertEquals(List.of(alice, zed), model.getFilteredPersonList());
        assertEquals(List.of(zed, alice), model.getAddressBook().getPersonList());
        assertFalse(command.isDataChanging());
    }

    /**
     * Checks that a command cannot be constructed or executed with missing dependencies.
     */
    @Test
    public void constructorAndExecute_nullArguments_rejectsInput() {
        assertThrows(NullPointerException.class, () -> new FilterCommand(null));
        assertThrows(NullPointerException.class, () -> new FilterCommand(predicate).execute(null));
    }

    /**
     * Checks that equal commands with independently constructed criteria behave as one hash-set entry.
     */
    @Test
    public void hashCode_equalCriteria_supportsHashSetLookup() {
        FilterCommand command = new FilterCommand(predicate);
        FilterCommand equivalent = new FilterCommand(
                new ContactMatchesCriteriaPredicate(List.of("Alice"), Optional.empty(), Optional.empty()));
        Set<FilterCommand> commands = new HashSet<>();
        commands.add(command);

        assertEquals(command, equivalent);
        assertEquals(command.hashCode(), equivalent.hashCode());
        assertTrue(commands.contains(equivalent));
        assertFalse(commands.add(equivalent));
        assertEquals(1, commands.size());
    }

    /**
     * Checks command equality and diagnostic output against the supplied criteria.
     */
    @Test
    public void equals_comparesCriteria() {
        FilterCommand command = new FilterCommand(predicate);
        assertEquals(command, command);
        assertEquals(command, new FilterCommand(predicate));
        assertFalse(command.equals(new FilterCommand(
                new ContactMatchesCriteriaPredicate(List.of("Bob"), Optional.empty(), Optional.empty()))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
        assertTrue(command.toString().contains(predicate.toString()));
    }
}
