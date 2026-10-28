package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks application home discovery for packaged and development launches.
 */
public class AppPathsTest {
    @TempDir
    public Path temporaryFolder;

    /**
     * Checks that changing the working directory does not change a packaged application's home.
     */
    @Test
    public void resolveHomeDirectory_packagedJar_usesJarParent() throws IOException {
        Path home = Files.createDirectory(temporaryFolder.resolve("jar-home"));
        Path jar = Files.createFile(home.resolve("renamed-application.jar"));
        Path firstWorkingDirectory = Files.createDirectory(temporaryFolder.resolve("first"));
        Path secondWorkingDirectory = Files.createDirectory(temporaryFolder.resolve("second"));

        assertEquals(home, AppPaths.resolveHomeDirectory(jar.toUri(), firstWorkingDirectory));
        assertEquals(home, AppPaths.resolveHomeDirectory(jar.toUri(), secondWorkingDirectory));
    }

    /**
     * Checks that URI conversion preserves spaces, Unicode, and literal URL punctuation.
     */
    @Test
    public void resolveHomeDirectory_encodedJarLocation_preservesDirectoryName() throws IOException {
        Path home = Files.createDirectory(temporaryFolder.resolve("Tutor Contacts café %h # +"));
        Path jar = Files.createFile(home.resolve("Tutor Contacts.jar"));

        assertEquals(home, AppPaths.resolveHomeDirectory(jar.toUri(), temporaryFolder));
    }

    /**
     * Checks that development class directories keep using the normalized working directory.
     */
    @Test
    public void resolveHomeDirectory_classDirectory_usesDevelopmentDirectory() throws IOException {
        Path classes = Files.createDirectory(temporaryFolder.resolve("classes"));
        Path project = Files.createDirectory(temporaryFolder.resolve("project"));

        assertEquals(project, AppPaths.resolveHomeDirectory(classes.toUri(), project.resolve("child").resolve("..")));
    }

    /**
     * Checks that an unavailable code location cannot redirect application files to the working directory.
     */
    @Test
    public void resolveHomeDirectory_missingLocation_throwsIllegalStateException() {
        URI missingJar = temporaryFolder.resolve("missing.jar").toUri();

        assertThrows(IllegalStateException.class, () -> AppPaths.resolveHomeDirectory(missingJar, temporaryFolder));
    }

    /**
     * Checks that nonlocal code locations are rejected rather than interpreted as filesystem paths.
     */
    @Test
    public void resolveHomeDirectory_nonlocalLocation_throwsIllegalStateException() {
        URI location = URI.create("https://example.com/tutorcontacts.jar");

        assertThrows(IllegalStateException.class, () -> AppPaths.resolveHomeDirectory(location, temporaryFolder));
    }

    /**
     * Checks that the ordinary test runtime resolves to the current development directory.
     */
    @Test
    public void getHomeDirectory_developmentLaunch_usesWorkingDirectory() {
        assertEquals(Path.of("").toAbsolutePath().normalize(), AppPaths.getHomeDirectory());
    }
}
