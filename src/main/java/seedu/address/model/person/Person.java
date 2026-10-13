package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    private final Role role;

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Optional<Subject> subject;
    private final Optional<Level> level;
    private final Optional<Note> note;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Constructs a contact without the role-specific fields introduced for the TutorContacts MVP.
     *
     * <p>This constructor remains available while dependent command work is integrated. New code should use the
     * constructor that accepts subject, level, and note values.</p>
     */
    public Person(Role role, Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(role, name, phone, email, address, Optional.empty(), Optional.empty(), Optional.empty(), tags);
    }

    /**
     * Constructs a contact with all supported TutorContacts fields.
     */
    public Person(Role role, Name name, Phone phone, Email email, Address address, Optional<Subject> subject,
            Optional<Level> level, Optional<Note> note, Set<Tag> tags) {
        requireAllNonNull(role, name, phone, email, address, tags);
        requireAllNonNull(subject, level, note);
        this.role = role;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.subject = subject;
        this.level = level;
        this.note = note;
        this.tags.addAll(tags);
    }

    /**
     * Returns this contact's role.
     */
    public Role getRole() {
        return role;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns the subject taught to this contact, if applicable.
     */
    public Optional<Subject> getSubject() {
        return subject;
    }

    /**
     * Returns this contact's academic level, if applicable.
     */
    public Optional<Level> getLevel() {
        return level;
    }

    /**
     * Returns this contact's note, if present.
     */
    public Optional<Note> getNote() {
        return note;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both contacts have the same role and normalized name, and share a normalized phone or email.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getRole().equals(getRole())
                && otherPerson.getName().isSameName(getName())
                && (otherPerson.getPhone().isSamePhone(getPhone())
                        || otherPerson.getEmail().isSameEmail(getEmail()));
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return role.equals(otherPerson.role)
                && name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && subject.equals(otherPerson.subject)
                && level.equals(otherPerson.level)
                && note.equals(otherPerson.note)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(role, name, phone, email, address, subject, level, note, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("role", role)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("subject", subject)
                .add("level", level)
                .add("note", note)
                .add("tags", tags)
                .toString();
    }

}
