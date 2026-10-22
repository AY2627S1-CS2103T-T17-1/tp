package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_NULL_PERSON = "Persons list contains a null person.";
    public static final String MESSAGE_DUPLICATE_LINK = "Links list contains duplicate link(s).";
    public static final String MESSAGE_NULL_LINK = "Links list contains a null link.";
    public static final String MESSAGE_UNKNOWN_LINK_CONTACT =
            "Links list contains a link whose parent or student is not in the persons list.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedLink> links = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and links.
     * A missing {@code links} list is treated as empty so that files saved before links existed still load.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("links") List<JsonAdaptedLink> links) {
        this.persons.addAll(persons);
        if (links != null) {
            this.links.addAll(links);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        links.addAll(source.getLinkList().stream().map(JsonAdaptedLink::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            if (jsonAdaptedPerson == null) {
                throw new IllegalValueException(MESSAGE_NULL_PERSON);
            }
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        for (JsonAdaptedLink jsonAdaptedLink : links) {
            if (jsonAdaptedLink == null) {
                throw new IllegalValueException(MESSAGE_NULL_LINK);
            }
            ParentStudentLink link = jsonAdaptedLink.toModelType();
            if (!addressBook.hasContactsOf(link)) {
                throw new IllegalValueException(MESSAGE_UNKNOWN_LINK_CONTACT);
            }
            if (addressBook.hasLink(link)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_LINK);
            }
            addressBook.addLink(link);
        }
        return addressBook;
    }

}
