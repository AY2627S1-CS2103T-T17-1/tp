package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Tests application configuration that can be checked without starting JavaFX.
 */
public class MainAppTest {

    @Test
    public void addressBookFilePath_usesTutorContactsJson() throws ReflectiveOperationException {
        Field filePathField = MainApp.class.getDeclaredField("ADDRESS_BOOK_FILE_PATH");
        filePathField.setAccessible(true);

        assertEquals(Paths.get("data", "tutorcontacts.json"), filePathField.get(null));
    }
}
