package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks log-file placement and literal directory names without reconfiguring global logging.
 */
public class LogsCenterTest {
    @TempDir
    public Path temporaryFolder;

    /**
     * Checks that FileHandler pattern tokens in directory names cannot redirect application logs.
     */
    @Test
    public void createFileHandler_percentDirectory_writesLogInsideRequestedDirectory() throws IOException {
        Path directory = Files.createDirectory(temporaryFolder.resolve("logs café %h %t %g %u %%"));
        FileHandler handler = LogsCenter.createFileHandler(directory.resolve("addressbook.log"));
        try {
            handler.publish(new LogRecord(Level.INFO, "Application home log probe"));
            handler.flush();
        } finally {
            handler.close();
        }

        assertTrue(Files.readString(directory.resolve("addressbook.log.0")).contains("Application home log probe"));
        try (Stream<Path> files = Files.list(directory)) {
            assertEquals(List.of("addressbook.log.0"), files.map(path -> path.getFileName().toString()).toList());
        }
    }

    /**
     * Checks that an unusable log destination reports a controlled I/O failure.
     */
    @Test
    public void createFileHandler_missingParent_throwsIoException() {
        Path logFile = temporaryFolder.resolve("missing").resolve("addressbook.log");

        assertThrows(IOException.class, () -> LogsCenter.createFileHandler(logFile));
    }
}
