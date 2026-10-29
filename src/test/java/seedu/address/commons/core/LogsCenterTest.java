package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
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
     * Checks that successful initialization attaches a usable file handler to the supplied logger.
     *
     * @throws IOException If the saved log cannot be read.
     */
    @Test
    public void addFileHandler_validPath_attachesWorkingHandler() throws IOException {
        Logger targetLogger = Logger.getAnonymousLogger();
        targetLogger.setUseParentHandlers(false);
        targetLogger.setLevel(Level.ALL);
        Path logFile = temporaryFolder.resolve("addressbook.log");
        try {
            LogsCenter.addFileHandler(targetLogger, logFile);

            assertEquals(1, targetLogger.getHandlers().length);
            assertTrue(targetLogger.getHandlers()[0] instanceof FileHandler);
            targetLogger.info("File logging is available");
            targetLogger.getHandlers()[0].flush();
            assertTrue(Files.readString(temporaryFolder.resolve("addressbook.log.0"))
                    .contains("File logging is available"));
        } finally {
            for (Handler handler : targetLogger.getHandlers()) {
                targetLogger.removeHandler(handler);
                handler.close();
            }
        }
    }

    /**
     * Checks that file initialization failure emits a warning and preserves working fallback handlers.
     *
     * @throws IOException If the in-memory fallback handler cannot be configured.
     */
    @Test
    public void addFileHandler_unwritablePath_warnsAndPreservesExistingHandlers() throws IOException {
        Logger targetLogger = Logger.getAnonymousLogger();
        targetLogger.setUseParentHandlers(false);
        targetLogger.setLevel(Level.ALL);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamHandler fallback = new StreamHandler(output, new SimpleFormatter());
        fallback.setEncoding(StandardCharsets.UTF_8.name());
        fallback.setLevel(Level.ALL);
        targetLogger.addHandler(fallback);
        try {
            LogsCenter.addFileHandler(targetLogger, temporaryFolder.resolve("missing").resolve("addressbook.log"));

            assertEquals(1, targetLogger.getHandlers().length);
            assertSame(fallback, targetLogger.getHandlers()[0]);
            targetLogger.info("Fallback logging is still available");
            fallback.flush();
            String messages = output.toString(StandardCharsets.UTF_8);
            assertTrue(messages.contains("Could not open the application log file. Using console logging only:"));
            assertTrue(messages.contains("Fallback logging is still available"));
        } finally {
            targetLogger.removeHandler(fallback);
            fallback.close();
        }
    }

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
