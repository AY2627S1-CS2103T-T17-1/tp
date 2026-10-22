package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Tests identity matching and editing of {@code ParentStudentLink}.
 */
public class ParentStudentLinkTest {

    private final ParentStudentLink georgeAlice = ParentStudentLink.between(GEORGE, ALICE);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ParentStudentLink(null, GEORGE.getPhone(),
                ALICE.getName(), ALICE.getPhone()));
    }

    @Test
    public void between_wrongRoles_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ParentStudentLink.MESSAGE_CONSTRAINTS, () ->
                ParentStudentLink.between(ALICE, GEORGE));
        assertThrows(IllegalArgumentException.class, ParentStudentLink.MESSAGE_CONSTRAINTS, () ->
                ParentStudentLink.between(ALICE, BENSON));
    }

    @Test
    public void isParentAndIsStudent_matchingContacts_returnsTrue() {
        assertTrue(georgeAlice.isParent(GEORGE));
        assertTrue(georgeAlice.isStudent(ALICE));
        assertTrue(georgeAlice.involves(GEORGE));
        assertTrue(georgeAlice.involves(ALICE));
        assertFalse(georgeAlice.involves(BENSON));
    }

    @Test
    public void isParent_differentCaseAndPhoneFormat_returnsTrue() {
        Person reformattedGeorge = new PersonBuilder(GEORGE).withName("george best").withPhone("948 2442").build();

        assertTrue(georgeAlice.isParent(reformattedGeorge));
    }

    @Test
    public void isParent_sameNameAndPhoneButStudentRole_returnsFalse() {
        Person studentGeorge = new PersonBuilder(GEORGE).withRole("student").build();

        assertFalse(georgeAlice.isParent(studentGeorge));
        assertFalse(georgeAlice.isStudent(studentGeorge));
    }

    @Test
    public void isParent_differentPhone_returnsFalse() {
        Person otherGeorge = new PersonBuilder(GEORGE).withPhone("81234567").build();

        assertFalse(georgeAlice.isParent(otherGeorge));
    }

    @Test
    public void withContactReplaced_editedStudent_updatesStudentSide() {
        Person editedAlice = new PersonBuilder(ALICE).withName("Alice Tan").withPhone("81112222").build();

        ParentStudentLink updatedLink = georgeAlice.withContactReplaced(ALICE, editedAlice);

        assertEquals(ParentStudentLink.between(GEORGE, editedAlice), updatedLink);
    }

    @Test
    public void withContactReplaced_editedParent_updatesParentSide() {
        Person editedGeorge = new PersonBuilder(GEORGE).withPhone("81113333").build();

        ParentStudentLink updatedLink = georgeAlice.withContactReplaced(GEORGE, editedGeorge);

        assertEquals(ParentStudentLink.between(editedGeorge, ALICE), updatedLink);
    }

    @Test
    public void withContactReplaced_unrelatedContact_returnsSameLink() {
        Person editedBenson = new PersonBuilder(BENSON).withPhone("81114444").build();

        assertSame(georgeAlice, georgeAlice.withContactReplaced(BENSON, editedBenson));
    }

    @Test
    public void equals() {
        Person reformattedGeorge = new PersonBuilder(GEORGE).withName("GEORGE BEST").withPhone("948-2442").build();
        ParentStudentLink reformattedLink = ParentStudentLink.between(reformattedGeorge, ALICE);

        // same values -> returns true
        assertTrue(georgeAlice.equals(ParentStudentLink.between(GEORGE, ALICE)));

        // same normalized identity -> returns true with the same hash code
        assertTrue(georgeAlice.equals(reformattedLink));
        assertEquals(georgeAlice.hashCode(), reformattedLink.hashCode());

        // same object -> returns true
        assertTrue(georgeAlice.equals(georgeAlice));

        // null -> returns false
        assertFalse(georgeAlice.equals(null));

        // different type -> returns false
        assertFalse(georgeAlice.equals(5));

        // different student -> returns false
        assertFalse(georgeAlice.equals(ParentStudentLink.between(GEORGE, BENSON)));
    }
}
