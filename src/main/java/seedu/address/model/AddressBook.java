package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 * Parent-student links are kept beside the contacts so that deleting or editing a contact keeps its links consistent.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();

    /**
     * Links between parent and student contacts in this address book, without duplicates.
     */
    private final List<ParentStudentLink> links = new ArrayList<>();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setLinks(newData.getLinkList());
    }

    /**
     * Replaces the contents of the link list with {@code links}.
     * {@code links} must not contain duplicate links.
     */
    public void setLinks(List<ParentStudentLink> links) {
        requireNonNull(links);
        assert links.stream().distinct().count() == links.size() : "Links must not contain duplicates";

        // Copy first, as links may be a view of this.links (e.g. from getLinkList()) that clear() would empty.
        List<ParentStudentLink> replacement = new ArrayList<>(links);
        this.links.clear();
        this.links.addAll(replacement);
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        links.replaceAll(link -> link.withContactReplaced(target, editedPerson));
    }

    /**
     * Removes {@code key} and all of its links from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        links.removeIf(link -> link.involves(key));
    }

    //// link-level operations

    /**
     * Returns true if {@code link} already exists in the address book.
     */
    public boolean hasLink(ParentStudentLink link) {
        requireNonNull(link);
        return links.contains(link);
    }

    /**
     * Returns true if both the parent and the student identified by {@code link} exist in the address book.
     */
    public boolean hasContactsOf(ParentStudentLink link) {
        requireNonNull(link);
        return findPerson(link::isParent).isPresent() && findPerson(link::isStudent).isPresent();
    }

    /**
     * Adds a link to the address book.
     * The link must not already exist, and both of its contacts must exist in the address book.
     */
    public void addLink(ParentStudentLink link) {
        requireNonNull(link);
        assert !hasLink(link) : "Link must not already exist";
        assert hasContactsOf(link) : "Both contacts of a link must exist";

        links.add(link);
    }

    /**
     * Returns the contacts linked to {@code person}: the students of a parent, or the parents of a student.
     */
    public List<Person> getLinkedPersons(Person person) {
        requireNonNull(person);
        List<Person> linkedPersons = new ArrayList<>();
        for (ParentStudentLink link : links) {
            if (link.isParent(person)) {
                findPerson(link::isStudent).ifPresent(linkedPersons::add);
            } else if (link.isStudent(person)) {
                findPerson(link::isParent).ifPresent(linkedPersons::add);
            }
        }
        return linkedPersons;
    }

    private Optional<Person> findPerson(Predicate<Person> predicate) {
        return persons.asUnmodifiableObservableList().stream().filter(predicate).findFirst();
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("links", links)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public List<ParentStudentLink> getLinkList() {
        return Collections.unmodifiableList(links);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && links.equals(otherAddressBook.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, links);
    }
}
