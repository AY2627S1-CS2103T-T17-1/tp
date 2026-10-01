package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;

/**
 * Tests the remark command as it is introduced into the command execution path.
 */
public class RemarkCommandTest {

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemarkCommand(null, ""));
        assertThrows(NullPointerException.class, () -> new RemarkCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_notImplemented_reportsArguments() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, "Likes swimming");
        assertThrows(CommandException.class, "Index: 1, Remark: Likes swimming", () ->
                command.execute(new ModelManager()));
    }

    @Test
    public void equals() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, "Likes swimming");
        assertTrue(command.equals(command));
        assertTrue(command.equals(new RemarkCommand(INDEX_FIRST_PERSON, "Likes swimming")));
        assertFalse(command.equals(null));
        assertFalse(command.equals("remark"));
        assertFalse(command.equals(new RemarkCommand(INDEX_SECOND_PERSON, "Likes swimming")));
        assertFalse(command.equals(new RemarkCommand(INDEX_FIRST_PERSON, "Likes running")));
    }
}
