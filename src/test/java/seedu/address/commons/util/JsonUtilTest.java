package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON conversion and file loading.
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @TempDir
    public Path temporaryFolder;

    /**
     * Checks that only one complete JSON value is accepted.
     */
    @Test
    public void fromJsonString_trailingContent_throwsIoException() {
        String[] invalidDocuments = {"{} {}", "{} []", "{} null", "{} true", "{} 1", "{} \"text\"",
            "{} unexpected trailing content", "{} {", "{} ]", "null {}", "null garbage"};
        for (String json : invalidDocuments) {
            assertThrows(IOException.class, () -> JsonUtil.fromJsonString(json, UserPrefs.class), json);
        }
    }

    /**
     * Checks that optional fields, unknown fields, and JSON whitespace remain supported.
     */
    @Test
    public void fromJsonString_validObject_returnsDefaultPreferences() throws IOException {
        String[] validDocuments = {"{}", " \r\n\t{} \r\n\t", "{\"futureSetting\": [1, {\"value\": true}]}"};
        for (String json : validDocuments) {
            assertEquals(new UserPrefs(), JsonUtil.fromJsonString(json, UserPrefs.class), json);
        }
    }

    /**
     * Checks that scalar JSON values and apparent trailing text inside strings remain supported.
     */
    @Test
    public void fromJsonString_scalarValue_returnsValue() throws IOException {
        assertEquals("{} trailing [null]", JsonUtil.fromJsonString("\"{} trailing [null]\" \n", String.class));
        assertEquals(42, JsonUtil.fromJsonString("42 \t", Integer.class));
        assertEquals(true, JsonUtil.fromJsonString("true \r\n", Boolean.class));
    }

    /**
     * Checks that generic JSON conversion retains its nullable result contract.
     */
    @Test
    public void fromJsonString_nullValue_returnsNull() throws IOException {
        assertNull(JsonUtil.fromJsonString("null \r\n", UserPrefs.class));
    }

    /**
     * Checks that an existing null-root file is rejected without changing its contents.
     */
    @Test
    public void readJsonFile_nullRoot_throwsDataLoadingException() throws IOException {
        Path dataPath = temporaryFolder.resolve("data.json");
        for (String json : new String[]{"null", " \r\nnull\t "}) {
            Files.writeString(dataPath, json);

            assertThrows(DataLoadingException.class, () -> JsonUtil.readJsonFile(dataPath, UserPrefs.class), json);
            assertEquals(json, Files.readString(dataPath));
        }
    }

    /**
     * Checks that a missing file is distinct from an existing invalid file.
     */
    @Test
    public void readJsonFile_missingFile_returnsEmptyWithoutCreatingFile() throws DataLoadingException {
        Path dataPath = temporaryFolder.resolve("missing.json");

        assertTrue(JsonUtil.readJsonFile(dataPath, UserPrefs.class).isEmpty());
        assertTrue(Files.notExists(dataPath));
    }

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    //TODO: @Test jsonUtil_readJsonStringToObjectInstance_correctObject()

    //TODO: @Test jsonUtil_writeThenReadObjectToJson_correctObject()
}
