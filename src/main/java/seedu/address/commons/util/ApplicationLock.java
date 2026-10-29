package seedu.address.commons.util;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Holds an operating-system file lock that prevents simultaneous access to one application home.
 */
public final class ApplicationLock implements AutoCloseable {

    private final FileChannel channel;
    private final FileLock lock;

    private ApplicationLock(FileChannel channel, FileLock lock) {
        this.channel = channel;
        this.lock = lock;
    }

    /**
     * Tries to acquire an exclusive lock on {@code lockFilePath}.
     *
     * <p>The lock file is intentionally retained after release. File presence alone does not indicate that an
     * application is running; only the operating-system lock is authoritative.</p>
     *
     * @param lockFilePath File used to coordinate application instances.
     * @return The acquired lock, or an empty optional if another instance holds it.
     * @throws IOException If the lock file cannot be opened or checked.
     */
    public static Optional<ApplicationLock> tryAcquire(Path lockFilePath) throws IOException {
        requireNonNull(lockFilePath);

        Path absolutePath = lockFilePath.toAbsolutePath().normalize();
        Path parentDirectory = absolutePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        FileChannel channel = FileChannel.open(absolutePath, CREATE, WRITE);
        try {
            FileLock lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                return Optional.empty();
            }
            return Optional.of(new ApplicationLock(channel, lock));
        } catch (OverlappingFileLockException e) {
            channel.close();
            return Optional.empty();
        } catch (IOException | RuntimeException e) {
            channel.close();
            throw e;
        }
    }

    /**
     * Releases the application lock and closes its file channel.
     *
     * @throws IOException If the lock or channel cannot be closed.
     */
    @Override
    public void close() throws IOException {
        try {
            if (lock.isValid()) {
                lock.release();
            }
        } finally {
            channel.close();
        }
    }
}
