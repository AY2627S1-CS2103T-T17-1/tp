package seedu.address.model;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns true if {@code link} already exists in the address book.
     */
    boolean hasLink(ParentStudentLink link);

    /**
     * Adds the given link.
     * {@code link} must not already exist, and both of its contacts must exist in the address book.
     */
    void addLink(ParentStudentLink link);

    /**
     * Removes the given link. Both of its contacts are kept.
     * {@code link} must exist in the address book.
     */
    void removeLink(ParentStudentLink link);

    /**
     * Returns the contacts linked to {@code person}: the students of a parent, or the parents of a student.
     */
    List<Person> getLinkedPersons(Person person);

    /**
     * Returns the observable, unmodifiable displayed list after filtering and ordering.
     *
     * @return The live view used for displayed contact indices.
     */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Returns the predicate used to determine which contacts are displayed.
     *
     * @return The active contact filter, or a predicate that shows all contacts.
     */
    Predicate<Person> getFilteredPersonPredicate();

    /**
     * Updates the displayed list's filter without changing its ordering.
     *
     * @param predicate Criteria that displayed contacts must satisfy.
     * @throws NullPointerException If {@code predicate} is {@code null}.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns the active display comparator.
     *
     * @return The active comparator, or {@code null} when contacts follow insertion order.
     */
    Comparator<? super Person> getPersonListComparator();

    /**
     * Changes the displayed order without changing the filter or stored contacts.
     *
     * @param comparator Ordering to apply, or {@code null} to restore insertion order.
     */
    void setPersonListComparator(Comparator<? super Person> comparator);
}
