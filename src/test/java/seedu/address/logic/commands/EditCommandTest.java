package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit)
                .withName(PersonBuilder.DEFAULT_NAME)
                .withPhone(PersonBuilder.DEFAULT_PHONE)
                .withEmail(PersonBuilder.DEFAULT_EMAIL)
                .withAddress(PersonBuilder.DEFAULT_ADDRESS)
                .withTags()
                .build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, duplicateMessage(firstPerson));
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, duplicateMessage(personInList));
    }

    @Test
    public void execute_duplicateOfOneSameNameContact_namesTheClashingContactsPhone() {
        Model sameNameModel = new ModelManager();
        Person firstAlex = new PersonBuilder().withName("Alex Tan").withPhone("91111111")
                .withEmail("alex.first@example.com").build();
        Person secondAlex = new PersonBuilder().withName("Alex Tan").withPhone("92222222")
                .withEmail("alex.second@example.com").build();
        Person bea = new PersonBuilder().withName("Bea Tan").withPhone("93333333").withEmail("bea@example.com").build();
        sameNameModel.addPerson(firstAlex);
        sameNameModel.addPerson(secondAlex);
        sameNameModel.addPerson(bea);

        EditCommand editCommand = new EditCommand(Index.fromOneBased(3),
                new EditPersonDescriptorBuilder().withName("Alex Tan").withPhone("92222222").build());

        // Both existing contacts are named Alex Tan, so only the phone shows which one clashes.
        assertCommandFailure(editCommand, sameNameModel,
                "This contact duplicates an existing student: Alex Tan (92222222).");
    }

    @Test
    public void execute_phoneEditedToMatchSameNameContact_failure() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person otherPerson = new PersonBuilder(personToEdit).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).build();
        model.addPerson(otherPerson);

        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build());

        // The message names the existing contact that the edit would duplicate.
        assertCommandFailure(editCommand, model, "This contact duplicates an existing "
                + otherPerson.getRole() + ": " + otherPerson.getName() + " (" + VALID_PHONE_BOB + ").");
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Checks that editing student-only fields and a note leaves unspecified contact details intact.
     */
    @Test
    public void execute_studentRoleSpecificFields_updatesOnlySpecifiedFields() {
        Person editedPerson = new PersonBuilder(ALICE).withSubject("A-Math")
                .withLevel("Secondary 4").withNote("Prefers examples").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withSubject("A-Math").withLevel("Secondary 4").withNote("Prefers examples").build();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    /**
     * Checks that a subject-only edit preserves the student's academic level and note.
     */
    @Test
    public void execute_studentSubjectOnly_preservesLevelAndNote() {
        model = new ModelManager();
        Person original = new PersonBuilder().withNote("Call after class").build();
        model.addPerson(original);
        Person editedPerson = new PersonBuilder(original).withSubject("Physics").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withSubject("Physics").build();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, editedPerson);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    /**
     * Checks that an explicitly empty note removes the existing note.
     */
    @Test
    public void execute_emptyNote_clearsExistingNote() {
        model = new ModelManager();
        Person original = new PersonBuilder().withNote("Call after class").build();
        model.addPerson(original);
        Person editedPerson = new PersonBuilder(original).withoutNote().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withoutNote().build();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, editedPerson);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    /**
     * Checks that a tutor can update the note on a parent contact.
     */
    @Test
    public void execute_parentNote_success() {
        model = new ModelManager();
        Person original = new PersonBuilder().withRole("parent").build();
        model.addPerson(original);
        Person editedPerson = new PersonBuilder(original).withNote("Contact after 6 pm").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withNote("Contact after 6 pm").build();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, editedPerson);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    /**
     * Checks that student-only fields cannot be assigned to a parent or partially change its note.
     */
    @Test
    public void execute_parentStudentFields_failureWithoutMutation() {
        model = new ModelManager();
        Person parent = new PersonBuilder().withRole("parent").build();
        model.addPerson(parent);

        EditPersonDescriptor subject = new EditPersonDescriptorBuilder().withSubject("Mathematics").build();
        EditPersonDescriptor levelAndNote = new EditPersonDescriptorBuilder()
                .withLevel("Secondary 3").withNote("Do not change").build();

        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, subject), model, AddCommand.MESSAGE_PARENT_FIELDS);
        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, levelAndNote), model,
                AddCommand.MESSAGE_PARENT_FIELDS);
    }

    /**
     * Checks that a legacy student without subject or level can still receive an unrelated edit.
     */
    @Test
    public void execute_legacyStudentWithoutSubjectOrLevel_allowsUnrelatedEdit() {
        model = new ModelManager();
        Person original = new PersonBuilder().withoutSubject().withoutLevel().build();
        model.addPerson(original);
        Person editedPerson = new PersonBuilder(original).withNote("Call after class").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withNote("Call after class").build();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, editedPerson);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                CommandResult.selectContact(expectedMessage, editedPerson), expectedModel);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    /**
     * Returns the error expected when an edit would duplicate {@code existingPerson}.
     */
    private static String duplicateMessage(Person existingPerson) {
        return String.format(EditCommand.MESSAGE_DUPLICATE_PERSON, existingPerson.getRole(), existingPerson.getName(),
                existingPerson.getPhone());
    }
}
