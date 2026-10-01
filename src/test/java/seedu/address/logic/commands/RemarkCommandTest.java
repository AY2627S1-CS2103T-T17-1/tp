package seedu.address.logic.commands;

import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;

/**
 * Tests the remark command as it is introduced into the command execution path.
 */
public class RemarkCommandTest {

    @Test
    public void execute_notImplemented_throwsCommandException() {
        assertThrows(CommandException.class, RemarkCommand.MESSAGE_NOT_IMPLEMENTED_YET, () ->
                new RemarkCommand().execute(new ModelManager()));
    }
}
