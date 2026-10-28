package seedu.address.testutil;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Subject;
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Creates a builder with the editable details of {@code person}.
     *
     * @param person Contact whose editable details are copied into the descriptor.
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        descriptor.setPhone(person.getPhone());
        descriptor.setEmail(person.getEmail());
        descriptor.setAddress(person.getAddress());
        person.getSubject().ifPresent(descriptor::setSubject);
        person.getLevel().ifPresent(descriptor::setLevel);
        descriptor.setNote(person.getNote());
        descriptor.setTags(person.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Sets the student subject to include in the edit descriptor.
     *
     * @param subject Subject to assign to the student contact.
     * @return This builder for chaining additional edit fields.
     */
    public EditPersonDescriptorBuilder withSubject(String subject) {
        descriptor.setSubject(new Subject(subject));
        return this;
    }

    /**
     * Records an empty subject prefix in the edit descriptor.
     */
    public EditPersonDescriptorBuilder withEmptySubject() {
        descriptor.setEmptySubject();
        return this;
    }

    /**
     * Sets the student academic level to include in the edit descriptor.
     *
     * @param level Academic level to assign to the student contact.
     * @return This builder for chaining additional edit fields.
     */
    public EditPersonDescriptorBuilder withLevel(String level) {
        descriptor.setLevel(new Level(level));
        return this;
    }

    /**
     * Records an empty level prefix in the edit descriptor.
     */
    public EditPersonDescriptorBuilder withEmptyLevel() {
        descriptor.setEmptyLevel();
        return this;
    }

    /**
     * Sets the contact note to include in the edit descriptor.
     *
     * @param note Note to assign to the student or parent contact.
     * @return This builder for chaining additional edit fields.
     */
    public EditPersonDescriptorBuilder withNote(String note) {
        descriptor.setNote(Optional.of(new Note(note)));
        return this;
    }

    /**
     * Marks the contact note for removal by the edit command.
     *
     * @return This builder for chaining additional edit fields.
     */
    public EditPersonDescriptorBuilder withoutNote() {
        descriptor.setNote(Optional.empty());
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    public EditPersonDescriptor build() {
        return descriptor;
    }
}
