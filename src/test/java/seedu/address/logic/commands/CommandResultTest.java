package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

/**
 * Tests command feedback, application flags, and contact-list presentation requests.
 */
public class CommandResultTest {
    /**
     * Checks that presentation actions and their target contacts participate in value equality.
     */
    @Test
    public void contactActions_distinctRequests_preserveTargetAndValueEquality() {
        CommandResult selected = CommandResult.selectContact("feedback", ALICE);
        CommandResult expanded = CommandResult.expandContact("feedback", ALICE);
        CommandResult collapsed = CommandResult.collapseContacts("feedback");

        assertEquals(CommandResult.ContactListAction.SELECT, selected.getContactListAction());
        assertEquals(CommandResult.ContactListAction.EXPAND, expanded.getContactListAction());
        assertEquals(CommandResult.ContactListAction.COLLAPSE, collapsed.getContactListAction());
        assertEquals(ALICE, expanded.getContact().orElseThrow());
        assertTrue(collapsed.getContact().isEmpty());
        assertNotEquals(selected, expanded);
        assertNotEquals(selected, CommandResult.selectContact("feedback", BOB));
        assertNotEquals(collapsed, new CommandResult("feedback"));
        assertEquals(expanded, CommandResult.expandContact("feedback", ALICE));
        assertEquals(expanded.hashCode(), CommandResult.expandContact("feedback", ALICE).hashCode());
    }

    /**
     * Checks that selection and expansion always require a target contact.
     */
    @Test
    public void contactActions_nullTarget_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CommandResult.selectContact("feedback", null));
        assertThrows(NullPointerException.class, () -> CommandResult.expandContact("feedback", null));
    }

    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + ", contactListAction=" + commandResult.getContactListAction()
                + ", contact=" + commandResult.getContact() + "}";
        assertEquals(expected, commandResult.toString());
    }
}
