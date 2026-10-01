package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.ModelManager;

/**
 * Tests the remark command as it is introduced into the command execution path.
 */
public class RemarkCommandTest {

    @Test
    public void execute_minimalCommand_returnsGreeting() {
        assertEquals("Hello from remark", new RemarkCommand().execute(new ModelManager()).getFeedbackToUser());
    }
}
