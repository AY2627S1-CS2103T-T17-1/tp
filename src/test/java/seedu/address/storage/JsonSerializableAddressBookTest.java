package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.testutil.TypicalPersons;

/**
 * Tests conversion of saved contact data into an address book.
 */
public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path LINKED_PERSONS_FILE = TEST_DATA_FOLDER.resolve("linkedPersonsAddressBook.json");
    private static final Path DUPLICATE_LINK_FILE = TEST_DATA_FOLDER.resolve("duplicateLinkAddressBook.json");
    private static final Path UNKNOWN_LINK_CONTACT_FILE =
            TEST_DATA_FOLDER.resolve("unknownLinkContactAddressBook.json");
    private static final Path INVALID_LINK_FILE = TEST_DATA_FOLDER.resolve("invalidLinkAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_linkedPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(LINKED_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook expected = new AddressBook();
        expected.addPerson(GEORGE);
        expected.addPerson(ALICE);
        expected.addLink(ParentStudentLink.between(GEORGE, ALICE));

        assertEquals(expected, dataFromFile.toModelType());
    }

    @Test
    public void toModelType_duplicateLinks_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_LINK_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_LINK,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_linkToUnknownContact_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(UNKNOWN_LINK_CONTACT_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_UNKNOWN_LINK_CONTACT,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_invalidLink_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_LINK_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_nullLink_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(Collections.emptyList(),
                Collections.singletonList(null));

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_LINK,
                data::toModelType);
    }

    @Test
    public void toModelType_nullPerson_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(Collections.singletonList(null), null);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_PERSON,
                data::toModelType);
    }

}
