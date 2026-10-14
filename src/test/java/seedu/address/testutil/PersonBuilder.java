package seedu.address.testutil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Subject;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_ROLE = "student";
    public static final String DEFAULT_SUBJECT = "Mathematics";
    public static final String DEFAULT_LEVEL = "Secondary 3";

    private Role role;
    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Optional<Subject> subject;
    private Optional<Level> level;
    private Optional<Note> note;
    private Set<Tag> tags;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        role = Role.fromString(DEFAULT_ROLE);
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        subject = Optional.of(new Subject(DEFAULT_SUBJECT));
        level = Optional.of(new Level(DEFAULT_LEVEL));
        note = Optional.empty();
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        role = personToCopy.getRole();
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        subject = personToCopy.getSubject();
        level = personToCopy.getLevel();
        note = personToCopy.getNote();
        tags = new HashSet<>(personToCopy.getTags());
    }

    /**
     * Sets the {@code Role} of the {@code Person} that we are building.
     */
    public PersonBuilder withRole(String role) {
        this.role = Role.fromString(role);
        if (this.role == Role.PARENT) {
            subject = Optional.empty();
            level = Optional.empty();
        }
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code Subject} of the {@code Person} that we are building.
     */
    public PersonBuilder withSubject(String subject) {
        this.subject = Optional.of(new Subject(subject));
        return this;
    }

    /**
     * Removes the subject from the {@code Person} that we are building.
     */
    public PersonBuilder withoutSubject() {
        subject = Optional.empty();
        return this;
    }

    /**
     * Sets the {@code Level} of the {@code Person} that we are building.
     */
    public PersonBuilder withLevel(String level) {
        this.level = Optional.of(new Level(level));
        return this;
    }

    /**
     * Removes the academic level from the {@code Person} that we are building.
     */
    public PersonBuilder withoutLevel() {
        level = Optional.empty();
        return this;
    }

    /**
     * Sets the {@code Note} of the {@code Person} that we are building.
     */
    public PersonBuilder withNote(String note) {
        this.note = Optional.of(new Note(note));
        return this;
    }

    /**
     * Removes the note from the {@code Person} that we are building.
     */
    public PersonBuilder withoutNote() {
        note = Optional.empty();
        return this;
    }

    /**
     * Builds the configured {@code Person}.
     */
    public Person build() {
        return new Person(role, name, phone, email, address, subject, level, note, tags);
    }

}
