package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void addLink_linkWithExistingContacts_linksBothDirections() {
        addressBook.addPerson(GEORGE);
        addressBook.addPerson(ALICE);
        ParentStudentLink link = ParentStudentLink.between(GEORGE, ALICE);

        addressBook.addLink(link);

        assertTrue(addressBook.hasLink(link));
        assertEquals(List.of(ALICE), addressBook.getLinkedPersons(GEORGE));
        assertEquals(List.of(GEORGE), addressBook.getLinkedPersons(ALICE));
    }

    @Test
    public void hasContactsOf_missingStudent_returnsFalse() {
        addressBook.addPerson(GEORGE);

        assertFalse(addressBook.hasContactsOf(ParentStudentLink.between(GEORGE, ALICE)));
    }

    @Test
    public void hasContactsOf_missingParent_returnsFalse() {
        addressBook.addPerson(ALICE);

        assertFalse(addressBook.hasContactsOf(ParentStudentLink.between(GEORGE, ALICE)));
    }

    @Test
    public void addLink_duplicateLink_throwsAssertionError() {
        addLinkedGeorgeAliceAndBenson();

        assertThrows(AssertionError.class, () -> addressBook.addLink(ParentStudentLink.between(GEORGE, ALICE)));
    }

    @Test
    public void addLink_missingContact_throwsAssertionError() {
        addressBook.addPerson(GEORGE);

        assertThrows(AssertionError.class, () -> addressBook.addLink(ParentStudentLink.between(GEORGE, ALICE)));
    }

    @Test
    public void setLinks_duplicateLinks_throwsAssertionError() {
        ParentStudentLink link = ParentStudentLink.between(GEORGE, ALICE);

        assertThrows(AssertionError.class, () -> addressBook.setLinks(List.of(link, link)));
    }

    @Test
    public void getLinkedPersons_parentWithTwoStudents_returnsBothStudents() {
        addLinkedGeorgeAliceAndBenson();

        assertEquals(List.of(ALICE, BENSON), addressBook.getLinkedPersons(GEORGE));
        assertEquals(List.of(), addressBook.getLinkedPersons(new PersonBuilder().build()));
    }

    @Test
    public void removePerson_linkedStudent_removesOnlyItsLinks() {
        addLinkedGeorgeAliceAndBenson();

        addressBook.removePerson(ALICE);

        assertEquals(List.of(ParentStudentLink.between(GEORGE, BENSON)), addressBook.getLinkList());
        assertEquals(List.of(BENSON), addressBook.getLinkedPersons(GEORGE));
    }

    @Test
    public void removePerson_linkedParent_removesAllItsLinks() {
        addLinkedGeorgeAliceAndBenson();

        addressBook.removePerson(GEORGE);

        assertEquals(List.of(), addressBook.getLinkList());
        assertEquals(List.of(), addressBook.getLinkedPersons(ALICE));
    }

    @Test
    public void setPerson_linkedStudentNameAndPhoneEdited_keepsLink() {
        addLinkedGeorgeAliceAndBenson();
        Person editedAlice = new PersonBuilder(ALICE).withName("Alice Tan").withPhone("81112222").build();

        addressBook.setPerson(ALICE, editedAlice);

        assertTrue(addressBook.hasLink(ParentStudentLink.between(GEORGE, editedAlice)));
        assertFalse(addressBook.hasLink(ParentStudentLink.between(GEORGE, ALICE)));
        assertEquals(List.of(editedAlice, BENSON), addressBook.getLinkedPersons(GEORGE));
    }

    @Test
    public void setPerson_linkedParentPhoneEdited_keepsLinks() {
        addLinkedGeorgeAliceAndBenson();
        Person editedGeorge = new PersonBuilder(GEORGE).withPhone("81113333").build();

        addressBook.setPerson(GEORGE, editedGeorge);

        assertEquals(List.of(ALICE, BENSON), addressBook.getLinkedPersons(editedGeorge));
        assertEquals(List.of(editedGeorge), addressBook.getLinkedPersons(ALICE));
    }

    @Test
    public void resetData_withLinks_copiesLinks() {
        addLinkedGeorgeAliceAndBenson();
        AddressBook copy = new AddressBook();

        copy.resetData(addressBook);

        assertEquals(addressBook, copy);
        assertEquals(addressBook.getLinkList(), copy.getLinkList());
    }

    @Test
    public void setLinks_ownLinkList_keepsLinks() {
        addLinkedGeorgeAliceAndBenson();
        List<ParentStudentLink> expectedLinks = List.copyOf(addressBook.getLinkList());

        addressBook.setLinks(addressBook.getLinkList());

        assertEquals(expectedLinks, addressBook.getLinkList());
    }

    @Test
    public void resetData_itself_keepsLinks() {
        addLinkedGeorgeAliceAndBenson();
        List<ParentStudentLink> expectedLinks = List.copyOf(addressBook.getLinkList());

        addressBook.resetData(addressBook);

        assertEquals(expectedLinks, addressBook.getLinkList());
    }

    @Test
    public void equals_differentLinks_returnsFalse() {
        addLinkedGeorgeAliceAndBenson();
        AddressBook sameContactsWithoutLinks = new AddressBook();
        sameContactsWithoutLinks.setPersons(addressBook.getPersonList());

        assertFalse(addressBook.equals(sameContactsWithoutLinks));
    }

    @Test
    public void equals() {
        addLinkedGeorgeAliceAndBenson();
        AddressBook copy = new AddressBook(addressBook);

        assertTrue(addressBook.equals(addressBook));
        assertTrue(addressBook.equals(copy));
        assertEquals(addressBook.hashCode(), copy.hashCode());
        assertFalse(addressBook.equals(null));
        assertFalse(addressBook.equals(5));
    }

    @Test
    public void getLinkList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getLinkList().clear());
    }

    private void addLinkedGeorgeAliceAndBenson() {
        addressBook.addPerson(GEORGE);
        addressBook.addPerson(ALICE);
        addressBook.addPerson(BENSON);
        addressBook.addLink(ParentStudentLink.between(GEORGE, ALICE));
        addressBook.addLink(ParentStudentLink.between(GEORGE, BENSON));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList()
                + ", links=" + addressBook.getLinkList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public List<ParentStudentLink> getLinkList() {
            return List.of();
        }
    }

}
