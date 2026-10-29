package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests operating-system-backed application instance coordination.
 */
public class ApplicationLockTest {

    @TempDir
    private Path temporaryFolder;

    @Test
    public void tryAcquire_availableFile_acquiresLock() throws Exception {
        Optional<ApplicationLock> acquiredLock = ApplicationLock.tryAcquire(lockFilePath());

        assertTrue(acquiredLock.isPresent());
        acquiredLock.orElseThrow().close();
    }

    @Test
    public void tryAcquire_heldFile_returnsEmpty() throws Exception {
        try (ApplicationLock ignored = ApplicationLock.tryAcquire(lockFilePath()).orElseThrow()) {
            assertTrue(ApplicationLock.tryAcquire(lockFilePath()).isEmpty());
        }
    }

    @Test
    public void close_releasesFileForNextInstance() throws Exception {
        ApplicationLock firstLock = ApplicationLock.tryAcquire(lockFilePath()).orElseThrow();
        firstLock.close();

        Optional<ApplicationLock> reacquiredLock = ApplicationLock.tryAcquire(lockFilePath());

        assertTrue(reacquiredLock.isPresent());
        reacquiredLock.orElseThrow().close();
    }

    /**
     * Returns the lock file used by one test.
     */
    private Path lockFilePath() {
        return temporaryFolder.resolve(".tutorcontacts.lock");
    }
}
