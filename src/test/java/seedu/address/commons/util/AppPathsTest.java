package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.cert.Certificate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks application home discovery for packaged and development launches.
 */
public class AppPathsTest {
    @TempDir
    public Path temporaryFolder;

    /**
     * Checks that a missing code source is reported instead of using the working directory.
     */
    @Test
    public void findHomeDirectory_missingSource_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, "Cannot determine the application code location.", () ->
                AppPaths.findHomeDirectory(null, temporaryFolder));
    }

    /**
     * Checks that a discovered source with no location is reported instead of selecting another folder.
     */
    @Test
    public void findHomeDirectory_missingSourceLocation_throwsIllegalStateException() {
        CodeSource source = new CodeSource(null, (Certificate[]) null);

        assertThrows(IllegalStateException.class, "Cannot determine the application code location.", () ->
                AppPaths.findHomeDirectory(source, temporaryFolder));
    }

    /**
     * Checks that a URL which cannot be converted to a URI produces the documented startup error.
     *
     * @throws IOException If the malformed-URI URL fixture cannot be constructed.
     */
    @Test
    public void findHomeDirectory_malformedSourceLocation_throwsIllegalStateException() throws IOException {
        // A URI factory would escape the space, so use the URL constructor only for this negative fixture.
        @SuppressWarnings("deprecation")
        URL location = new URL("file:/invalid path/tutorcontacts.jar");
        CodeSource source = new CodeSource(location, (Certificate[]) null);

        assertThrows(IllegalStateException.class, "Cannot determine the application home directory.", () ->
                AppPaths.findHomeDirectory(source, temporaryFolder));
    }

    /**
     * Checks that code-source discovery uses the JAR directory even when the working directory differs.
     *
     * @throws IOException If the disposable JAR fixture cannot be created.
     */
    @Test
    public void findHomeDirectory_packagedSource_usesJarParent() throws IOException {
        Path directory = Files.createDirectory(temporaryFolder.resolve("jar-home"));
        Path jar = Files.createFile(directory.resolve("tutorcontacts.jar"));
        CodeSource source = new CodeSource(jar.toUri().toURL(), (Certificate[]) null);

        assertEquals(directory, AppPaths.findHomeDirectory(source, temporaryFolder));
    }

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
