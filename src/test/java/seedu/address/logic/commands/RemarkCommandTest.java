package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests remark updates, displayed-list indexing, and command equality.
 */
public class RemarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemarkCommand(null, new Remark("")));
        assertThrows(NullPointerException.class, () -> new RemarkCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes swimming"));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_addRemarkUnfilteredList_success() {
        assertRemarkChange(INDEX_FIRST_PERSON, "Likes swimming");
    }

    @Test
    public void execute_replaceExistingRemark_success() {
        Person original = model.getFilteredPersonList().get(0);
        model.setPerson(original, new PersonBuilder(original).withRemark("Old remark").build());
        assertRemarkChange(INDEX_FIRST_PERSON, "New remark");
    }

    @Test
    public void execute_clearExistingRemark_success() {
        Person original = model.getFilteredPersonList().get(0);
        model.setPerson(original, new PersonBuilder(original).withRemark("Old remark").build());
        assertRemarkChange(INDEX_FIRST_PERSON, "");
    }

    @Test
    public void execute_clearEmptyRemark_success() {
        assertRemarkChange(INDEX_FIRST_PERSON, "");
    }

    @Test
    public void execute_filteredList_editsDisplayedPersonAndShowsAll() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        assertRemarkChange(INDEX_FIRST_PERSON, "Displayed person");
    }

    @Test
    public void execute_invalidUnfilteredIndex_failure() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new RemarkCommand(outOfBounds, new Remark("note")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidFilteredIndex_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(new RemarkCommand(INDEX_SECOND_PERSON, new Remark("note")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyAddressBook_failure() {
        assertCommandFailure(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("note")), new ModelManager(),
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Verifies that only the displayed person's remark changes and that the list filter is reset.
     */
    private void assertRemarkChange(Index index, String text) {
        Person original = model.getFilteredPersonList().get(index.getZeroBased());
        Person edited = new PersonBuilder(original).withRemark(text).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, edited);
        String format = text.isEmpty() ? RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS
                : RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS;
        assertCommandSuccess(new RemarkCommand(index, new Remark(text)), model,
                String.format(format, Messages.format(edited)), expectedModel);
    }

    @Test
    public void equals() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes swimming"));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes swimming"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("remark"));
        assertFalse(command.equals(new RemarkCommand(INDEX_SECOND_PERSON, new Remark("Likes swimming"))));
        assertFalse(command.equals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes running"))));
    }
}
