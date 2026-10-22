package seedu.address.model.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Subject;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(Role.STUDENT, new Name("Alex Yeoh"), new Phone("87438807"),
                new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"),
                Optional.of(new Subject("Mathematics")), Optional.of(new Level("Secondary 3")),
                Optional.of(new Note("Prefers visual examples")),
                getTagSet("friends")),
            new Person(Role.PARENT, new Name("Bernice Yu"), new Phone("99272758"),
                new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                Optional.empty(), Optional.empty(), Optional.of(new Note("Contact after 6 pm")),
                getTagSet("colleagues", "friends")),
            new Person(Role.STUDENT, new Name("Charlotte Oliveiro"), new Phone("93210283"),
                new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                Optional.of(new Subject("English")), Optional.of(new Level("Secondary 2")), Optional.empty(),
                getTagSet("neighbours")),
            new Person(Role.PARENT, new Name("David Li"), new Phone("91031282"),
                new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                Optional.empty(), Optional.empty(), Optional.empty(),
                getTagSet("family")),
            new Person(Role.STUDENT, new Name("Irfan Ibrahim"), new Phone("92492021"),
                new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"),
                Optional.of(new Subject("Science")), Optional.of(new Level("Primary 6")), Optional.empty(),
                getTagSet("classmates")),
            new Person(Role.PARENT, new Name("Roy Balakrishnan"), new Phone("92624417"),
                new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"),
                Optional.empty(), Optional.empty(), Optional.of(new Note("Prefers email")),
                getTagSet("colleagues"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        Person[] samplePersons = getSamplePersons();
        for (Person samplePerson : samplePersons) {
            sampleAb.addPerson(samplePerson);
        }
        // Bernice Yu is Alex Yeoh's parent, and David Li is Charlotte Oliveiro's parent.
        sampleAb.addLink(ParentStudentLink.between(samplePersons[1], samplePersons[0]));
        sampleAb.addLink(ParentStudentLink.between(samplePersons[3], samplePersons[2]));
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
