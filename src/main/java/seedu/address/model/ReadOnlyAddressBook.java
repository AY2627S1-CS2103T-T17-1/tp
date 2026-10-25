package seedu.address.model;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an unmodifiable view of the parent-student links.
     * This list will not contain any duplicate links.
     */
    List<ParentStudentLink> getLinkList();

}
