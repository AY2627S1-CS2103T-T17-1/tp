package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

/**
 * Tests command-driven selection and expansion without requiring a graphical display.
 */
public class ContactListStateTest {
    private final ContactListState state = new ContactListState();

    @Test
    void apply_addAfterView_selectsNewCompactContact() throws CommandException {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        state.apply(new ViewCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        state.apply(new AddCommand(BOB).execute(model), model.getFilteredPersonList());

        assertEquals(BOB, state.getSelectedContact().orElseThrow());
        assertFalse(state.isExpanded(ALICE));
        assertFalse(state.isExpanded(BOB));
    }

    @Test
    void apply_viewAnotherContact_onlyExpandsNewTarget() {
        state.apply(CommandResult.expandContact("view", ALICE), List.of(ALICE, BOB));
        state.apply(CommandResult.expandContact("view", BOB), List.of(ALICE, BOB));

        assertEquals(BOB, state.getSelectedContact().orElseThrow());
        assertFalse(state.isExpanded(ALICE));
        assertTrue(state.isExpanded(BOB));
    }

    @Test
    void apply_list_collapsesDetailsAndKeepsSelection() {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        state.apply(CommandResult.expandContact("view", ALICE), model.getFilteredPersonList());
        state.apply(new ListCommand().execute(model), model.getFilteredPersonList());

        assertEquals(ALICE, state.getSelectedContact().orElseThrow());
        assertFalse(state.isExpanded(ALICE));
    }

    @Test
    void apply_deleteEarlierThenSelectedContact_preservesThenClearsSelection() throws CommandException {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        state.apply(new ViewCommand(Index.fromOneBased(2)).execute(model), model.getFilteredPersonList());
        state.apply(new DeleteCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());

        assertEquals(BOB, state.getSelectedContact().orElseThrow());
        assertTrue(state.isExpanded(BOB));
        state.apply(new DeleteCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        assertTrue(state.getSelectedContact().isEmpty());
        assertFalse(state.isExpanded(BOB));
    }

    @Test
    void apply_filteredView_usesDisplayedIndexAndClearsHiddenSelection() throws CommandException {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        model.updateFilteredPersonList(BOB::equals);
        state.apply(new ViewCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        assertEquals(BOB, state.getSelectedContact().orElseThrow());

        model.updateFilteredPersonList(ALICE::equals);
        state.apply(new CommandResult("find"), model.getFilteredPersonList());
        assertTrue(state.getSelectedContact().isEmpty());
        assertFalse(state.isExpanded(BOB));
    }

    @Test
    void apply_invalidView_doesNotReplaceExpansion() {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        state.apply(CommandResult.expandContact("view", ALICE), model.getFilteredPersonList());

        assertThrows(CommandException.class, () -> state.apply(
                new ViewCommand(Index.fromOneBased(2)).execute(model), model.getFilteredPersonList()));
        assertEquals(ALICE, state.getSelectedContact().orElseThrow());
        assertTrue(state.isExpanded(ALICE));
    }

    @Test
    void select_differentContact_collapsesOldDetails() {
        state.apply(CommandResult.expandContact("view", ALICE), List.of(ALICE, BOB));
        state.select(ALICE);
        assertTrue(state.isExpanded(ALICE));
        state.select(BOB);
        assertFalse(state.isExpanded(ALICE));
        assertEquals(BOB, state.getSelectedContact().orElseThrow());
    }

    @Test
    void apply_emptyListWithoutSelection_staysEmpty() {
        state.apply(CommandResult.collapseContacts("list"), List.of());
        assertTrue(state.getSelectedContact().isEmpty());
    }

    /**
     * Checks that filtering preserves expansion when the selected contact moves to another displayed index.
     *
     * @throws Exception If executing a valid view or filter command unexpectedly fails.
     */
    @Test
    void apply_filterKeepsSelectedContact_preservesExpansionDespiteReordering() throws Exception {
        Model model = new ModelManager();
        model.addPerson(BOB);
        model.addPerson(ALICE);
        state.apply(new ViewCommand(Index.fromOneBased(2)).execute(model), model.getFilteredPersonList());
        state.apply(new AddressBookParser().parseCommand("filter s/Mathematics").execute(model),
                model.getFilteredPersonList());
        assertEquals(ALICE, model.getFilteredPersonList().getFirst());
        assertEquals(ALICE, state.getSelectedContact().orElseThrow());
        assertTrue(state.isExpanded(ALICE));
    }

    /**
     * Checks that filtering out the selected contact clears both selection and expansion.
     *
     * @throws Exception If executing a valid view or filter command unexpectedly fails.
     */
    @Test
    void apply_filterHidesSelectedContact_clearsSelectionAndExpansion() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        state.apply(new ViewCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        state.apply(new AddressBookParser().parseCommand("filter n/Bob").execute(model),
                model.getFilteredPersonList());
        assertTrue(state.getSelectedContact().isEmpty());
        assertFalse(state.isExpanded(ALICE));
    }

    /**
     * Checks that a rejected filter leaves the selected contact expanded.
     *
     * @throws Exception If the initial filter or view command unexpectedly fails.
     */
    @Test
    void apply_invalidFilter_preservesSelectionAndExpansion() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        AddressBookParser parser = new AddressBookParser();
        state.apply(parser.parseCommand("filter n/Alice").execute(model), model.getFilteredPersonList());
        state.apply(new ViewCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        assertThrows(ParseException.class, () -> parser.parseCommand("filter s/"));
        state.retainVisibleContacts(model.getFilteredPersonList());
        assertEquals(ALICE, state.getSelectedContact().orElseThrow());
        assertTrue(state.isExpanded(ALICE));
    }
}
