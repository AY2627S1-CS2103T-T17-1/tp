package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests command-driven selection without requiring a graphical display.
 */
public class ContactListStateTest {
    private final ContactListState state = new ContactListState();

    @Test
    void apply_addAfterView_selectsNewContact() throws CommandException {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        state.apply(new ViewCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        state.apply(new AddCommand(BOB).execute(model), model.getFilteredPersonList());

        assertEquals(BOB, state.getSelectedContact().orElseThrow());
    }

    @Test
    void apply_viewAnotherContact_selectsNewTarget() {
        state.apply(CommandResult.selectContact("view", ALICE), List.of(ALICE, BOB));
        state.apply(CommandResult.selectContact("view", BOB), List.of(ALICE, BOB));

        assertEquals(BOB, state.getSelectedContact().orElseThrow());
    }

    @Test
    void apply_list_keepsSelectionAndDetails() {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        state.apply(CommandResult.selectContact("view", ALICE), model.getFilteredPersonList());
        state.apply(new ListCommand().execute(model), model.getFilteredPersonList());

        assertEquals(ALICE, state.getSelectedContact().orElseThrow());
    }

    @Test
    void apply_deleteEarlierThenSelectedContact_preservesThenClearsSelection() throws CommandException {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        state.apply(new ViewCommand(Index.fromOneBased(2)).execute(model), model.getFilteredPersonList());
        state.apply(new DeleteCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());

        assertEquals(BOB, state.getSelectedContact().orElseThrow());
        state.apply(new DeleteCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        assertTrue(state.getSelectedContact().isEmpty());
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
    }

    @Test
    void apply_invalidView_doesNotReplaceSelection() {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        state.apply(CommandResult.selectContact("view", ALICE), model.getFilteredPersonList());

        assertThrows(CommandException.class, () -> state.apply(
                new ViewCommand(Index.fromOneBased(2)).execute(model), model.getFilteredPersonList()));
        assertEquals(ALICE, state.getSelectedContact().orElseThrow());
    }

    @Test
    void select_differentContact_changesDetailTarget() {
        state.apply(CommandResult.selectContact("view", ALICE), List.of(ALICE, BOB));
        state.select(ALICE);
        state.select(BOB);
        assertEquals(BOB, state.getSelectedContact().orElseThrow());
    }

    @Test
    void apply_emptyListWithoutSelection_staysEmpty() {
        state.apply(new CommandResult("list"), List.of());
        assertTrue(state.getSelectedContact().isEmpty());
    }

    /**
     * Checks that filtering preserves selection when the selected contact moves to another displayed index.
     *
     * @throws Exception If executing a valid view or filter command unexpectedly fails.
     */
    @Test
    void apply_filterKeepsSelectedContact_preservesSelectionDespiteReordering() throws Exception {
        Model model = new ModelManager();
        model.addPerson(BOB);
        model.addPerson(ALICE);
        state.apply(new ViewCommand(Index.fromOneBased(2)).execute(model), model.getFilteredPersonList());
        state.apply(new AddressBookParser().parseCommand("filter s/Mathematics").execute(model),
                model.getFilteredPersonList());
        assertEquals(ALICE, model.getFilteredPersonList().getFirst());
        assertEquals(ALICE, state.getSelectedContact().orElseThrow());
    }

    /**
     * Checks that filtering out the selected contact clears selection.
     *
     * @throws Exception If executing a valid view or filter command unexpectedly fails.
     */
    @Test
    void apply_filterHidesSelectedContact_clearsSelection() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        state.apply(new ViewCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        state.apply(new AddressBookParser().parseCommand("filter n/Bob").execute(model),
                model.getFilteredPersonList());
        assertTrue(state.getSelectedContact().isEmpty());
    }

    /**
     * Checks that a rejected filter leaves the selected contact unchanged.
     *
     * @throws Exception If the initial filter or view command unexpectedly fails.
     */
    @Test
    void apply_invalidFilter_preservesSelection() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        AddressBookParser parser = new AddressBookParser();
        state.apply(parser.parseCommand("filter n/Alice").execute(model), model.getFilteredPersonList());
        state.apply(new ViewCommand(Index.fromOneBased(1)).execute(model), model.getFilteredPersonList());
        assertThrows(ParseException.class, () -> parser.parseCommand("filter s/"));
        state.retainVisibleContacts(model.getFilteredPersonList());
        assertEquals(ALICE, state.getSelectedContact().orElseThrow());
    }

    /**
     * Checks that replacement metadata follows a selected edit even when every identity field changes.
     */
    @Test
    void apply_selectedEdit_followsReplacement() {
        Person edited = new PersonBuilder(ALICE)
                .withName("Renamed Contact").withPhone("98887777").withEmail("renamed@example.com").build();
        state.select(ALICE);
        state.apply(CommandResult.replaceContact("edit", ALICE, edited), List.of(edited, BOB));
        assertEquals(edited, state.getSelectedContact().orElseThrow());
    }

    /**
     * Checks that editing another contact does not steal selection.
     */
    @Test
    void apply_otherContactEdited_retainsSelection() {
        Person editedAlice = new PersonBuilder(ALICE).withNote("Updated note").build();
        state.select(BOB);
        state.apply(CommandResult.replaceContact("edit", ALICE, editedAlice), List.of(editedAlice, BOB));
        assertEquals(BOB, state.getSelectedContact().orElseThrow());
    }

    /**
     * Checks that restoring the full list does not resurrect a contact hidden by an earlier filter.
     */
    @Test
    void apply_listAfterHiddenSelection_staysUnselected() {
        state.select(ALICE);
        state.apply(new CommandResult("filter"), List.of(BOB));
        state.apply(new CommandResult("list"), List.of(ALICE, BOB));
        assertTrue(state.getSelectedContact().isEmpty());
    }
}
