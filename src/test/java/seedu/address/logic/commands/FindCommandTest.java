package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Checks command equality against the supplied name keywords.
     */
    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    /**
     * Checks feedback when no name keywords match.
     */
    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = "No matching contacts.";
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    /**
     * Checks feedback for a valid keyword that matches no stored contact.
     */
    @Test
    public void execute_unmatchedKeyword_noContactsFound() {
        NameContainsKeywordsPredicate predicate = preparePredicate("Zed");
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(new FindCommand(predicate), model, "No matching contacts.", expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    /**
     * Checks feedback for a search in an empty address book.
     */
    @Test
    public void execute_emptyAddressBook_noContactsFound() {
        model = new ModelManager();
        expectedModel = new ModelManager();
        NameContainsKeywordsPredicate predicate = preparePredicate("Carl");
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(new FindCommand(predicate), model, "No matching contacts.", expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    /**
     * Checks that one match uses the same contact-count wording as filtering.
     */
    @Test
    public void execute_singleKeyword_oneContactFound() {
        NameContainsKeywordsPredicate predicate = preparePredicate("Carl");
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(new FindCommand(predicate), model, "Showing 1 matching contacts.", expectedModel);
        assertEquals(List.of(CARL), model.getFilteredPersonList());
    }

    /**
     * Checks match-count feedback while retaining the OR search across keywords.
     */
    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = "Showing 3 matching contacts.";
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    /**
     * Checks that diagnostic output includes the name predicate.
     */
    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
