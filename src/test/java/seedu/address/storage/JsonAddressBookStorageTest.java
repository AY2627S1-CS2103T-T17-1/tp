package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ParentStudentLink;

/**
 * Tests loading and saving of contact data through JSON storage.
 */
public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_nullPerson_throwsDataLoadingExceptionWithoutChangingFile() throws Exception {
        Path dataPath = testFolder.resolve("tutorcontacts.json");
        String invalidData = "{\"persons\":[null]}";
        Files.writeString(dataPath, invalidData);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataPath);

        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(invalidData, Files.readString(dataPath));
    }

    /**
     * Checks that invalid roots and extra JSON content cannot load or alter the saved address book.
     */
    @Test
    public void readAddressBook_invalidDocument_throwsDataLoadingExceptionWithoutChangingFile()
            throws IOException {
        Path dataPath = testFolder.resolve("tutorcontacts.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataPath);
        for (String json : new String[]{"null", "{} trailing text", "{} {}", "{} null"}) {
            Files.writeString(dataPath, json);

            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertEquals(json, Files.readString(dataPath));
        }
    }

    @Test
    public void readAddressBook_nullTag_throwsDataLoadingExceptionWithoutChangingFile() throws Exception {
        Path dataPath = testFolder.resolve("tutorcontacts.json");
        String invalidData = "{\"persons\":[{\"role\":\"student\",\"name\":\"Alex Tan\",\"phone\":\"91234567\","
                + "\"email\":\"alex@example.com\",\"address\":\"1 Road\",\"tags\":[null]}]}";
        Files.writeString(dataPath, invalidData);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataPath);

        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(invalidData, Files.readString(dataPath));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

        // Save and read a parent-student link
        original.addLink(ParentStudentLink.between(GEORGE, BENSON));
        jsonAddressBookStorage.saveAddressBook(original);
        readBack = jsonAddressBookStorage.readAddressBook().get();
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void saveAddressBook_replacementFails_preservesExistingData() throws Exception {
        Path dataPath = testFolder.resolve("tutorcontacts.json");
        Files.createDirectory(dataPath);
        Path existingData = dataPath.resolve("existing-data.txt");
        Files.writeString(existingData, "keep this data");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataPath);

        assertThrows(IOException.class, () -> storage.saveAddressBook(new AddressBook()));

        assertTrue(Files.isDirectory(dataPath));
        assertEquals("keep this data", Files.readString(existingData));
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
}
