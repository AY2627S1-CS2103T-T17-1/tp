package seedu.address.commons.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;

/**
 * Resolves application files beneath the JAR directory, or the working directory for development launches.
 * Does not use logging because log initialization also depends on this class.
 */
public class AppPaths {
    private static final Path HOME_DIRECTORY = findHomeDirectory();

    private AppPaths() {
    }

    /**
     * Returns the absolute directory used for application data, preferences, and logs.
     * Packaged launches use the containing JAR directory, independently of the working directory.
     * IDE and Gradle launches from compiled class directories use the working directory.
     *
     * @return The normalized application home directory.
     */
    public static Path getHomeDirectory() {
        return HOME_DIRECTORY;
    }

    /**
     * Resolves the application home without falling back to another location if code discovery fails.
     *
     * @return The application home directory.
     * @throws IllegalStateException If the application code location is unavailable or invalid.
     */
    private static Path findHomeDirectory() {
        return findHomeDirectory(AppPaths.class.getProtectionDomain().getCodeSource(), Path.of(""));
    }

    /**
     * Resolves a discovered code source, reporting missing or malformed locations without choosing a fallback folder.
     *
     * @param source Discovered code source, which may be null if discovery is unavailable.
     * @param developmentDirectory Working directory to use for unpackaged development launches.
     * @return The normalized application home directory.
     * @throws IllegalStateException If the code location is missing, malformed, nonlocal, or unavailable.
     */
    static Path findHomeDirectory(CodeSource source, Path developmentDirectory) {
        if (source == null || source.getLocation() == null) {
            throw new IllegalStateException("Cannot determine the application code location.");
        }
        try {
            return resolveHomeDirectory(source.getLocation().toURI(), developmentDirectory);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot determine the application home directory.", e);
        }
    }

    /**
     * Resolves a local code location using the working directory only for unpackaged development classes.
     *
     * @param codeLocation Location of the application JAR or compiled class directory.
     * @param developmentDirectory Working directory to use for unpackaged development launches.
     * @return The normalized absolute home directory.
     * @throws IllegalStateException If the location is not a local file or does not exist.
     */
    static Path resolveHomeDirectory(URI codeLocation, Path developmentDirectory) {
        if (!"file".equalsIgnoreCase(codeLocation.getScheme())) {
            throw new IllegalStateException("Application code must be loaded from a local file.");
        }
        Path codePath = Path.of(codeLocation).toAbsolutePath().normalize();
        if (Files.isDirectory(codePath)) {
            return developmentDirectory.toAbsolutePath().normalize();
        }
        if (!Files.isRegularFile(codePath)) {
            throw new IllegalStateException("Application code location does not exist: " + codePath);
        }
        return codePath.getParent();
    }
}
