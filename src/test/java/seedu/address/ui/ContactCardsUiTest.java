package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises FXML cards and command-to-UI wiring on a desktop with {@code RUN_UI_TESTS=true}.
 * Core selection tests run separately without a display in {@link ContactListStateTest}.
 */
@EnabledIfEnvironmentVariable(named = "RUN_UI_TESTS", matches = "true")
public class ContactCardsUiTest {
    @TempDir
    public Path temporaryFolder;

    /**
     * Starts the existing JavaFX toolkit without adding a GUI testing dependency.
     */
    @BeforeAll
    static void startToolkit() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.countDown();
        });
        assertTrue(started.await(10, TimeUnit.SECONDS));
    }

    /**
     * Shuts down the toolkit after all desktop tests finish.
     */
    @AfterAll
    static void stopToolkit() {
        Platform.exit();
    }

    @Test
    void cards_compactAndExpanded_showApplicableFields() throws Exception {
        runOnFxThread(() -> {
            PersonCard compact = new PersonCard(ALICE, 1);
            assertEquals("Student", label(compact.getRoot(), "role").getText());
            assertEquals(ALICE.getName().fullName, label(compact.getRoot(), "name").getText());
            assertEquals(ALICE.getPhone().value, label(compact.getRoot(), "phone").getText());
            assertFalse(compact.getRoot().lookup("#details").isVisible());
            assertFalse(compact.getRoot().lookup("#details").isManaged());

            PersonCard student = new PersonCard(ALICE, 2, true);
            assertTrue(student.getRoot().lookup("#details").isVisible());
            assertTrue(student.getRoot().lookup("#details").isManaged());
            assertEquals("Subject: " + ALICE.getSubject().orElseThrow(), label(student.getRoot(), "subject").getText());
            assertEquals("Academic level: " + ALICE.getLevel().orElseThrow(),
                    label(student.getRoot(), "level").getText());
            assertEquals("Note: " + ALICE.getNote().orElseThrow(), label(student.getRoot(), "note").getText());
            assertEquals("Email: " + ALICE.getEmail(), label(student.getRoot(), "email").getText());
            assertEquals("Address: " + ALICE.getAddress(), label(student.getRoot(), "address").getText());

            PersonCard parent = new PersonCard(GEORGE, 3, true);
            assertEquals("Parent", label(parent.getRoot(), "role").getText());
            assertFalse(parent.getRoot().lookup("#subject").isVisible());
            assertFalse(parent.getRoot().lookup("#subject").isManaged());
            assertFalse(parent.getRoot().lookup("#level").isManaged());
            assertEquals("Note: " + GEORGE.getNote().orElseThrow(), label(parent.getRoot(), "note").getText());
        });
    }

    @Test
    void panel_deleteAndRollback_preservesContactRatherThanRowNumber() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Person> contacts = FXCollections.observableArrayList(ALICE, BOB);
            PersonListPanel panel = new PersonListPanel(contacts);
            new Scene(panel.getRoot(), 760, 600);
            panel.completeCommand(CommandResult.expandContact("view", BOB));
            panel.beginCommand();
            contacts.remove(ALICE);
            panel.completeCommand(new CommandResult("delete"));
            layout(panel.getRoot());
            assertEquals(BOB, list(panel.getRoot()).getSelectionModel().getSelectedItem());
            assertEquals("1.", label(selectedCard(panel.getRoot()), "id").getText());
            assertTrue(selectedCard(panel.getRoot()).lookup("#details").isVisible());

            panel.beginCommand();
            contacts.clear();
            contacts.add(BOB); // Simulates the logic layer restoring data after a failed save.
            panel.cancelCommand();
            layout(panel.getRoot());
            assertEquals(BOB, list(panel.getRoot()).getSelectionModel().getSelectedItem());
            assertTrue(selectedCard(panel.getRoot()).lookup("#details").isVisible());

            panel.beginCommand();
            contacts.clear();
            panel.completeCommand(new CommandResult("delete"));
            assertTrue(list(panel.getRoot()).getSelectionModel().isEmpty());
        });
    }

    @Test
    void window_commands_updateCardsAndPreserveExpansionOnInvalidInput() throws Exception {
        runOnFxThread(() -> {
            Model model = new ModelManager();
            model.addPerson(ALICE);
            model.addPerson(BOB);
            Path dataPath = temporaryFolder.resolve("contacts.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(model, storage), dataPath);
            window.fillInnerParts();
            Region root = (Region) stage.getScene().getRoot();
            root.resize(800, 600);

            enter(root, "view 2");
            assertEquals(BOB, list(root).getSelectionModel().getSelectedItem());
            assertTrue(selectedCard(root).lookup("#details").isVisible());
            saveSnapshot(root, "view-student");
            enter(root, "view 99");
            assertTrue(selectedCard(root).lookup("#details").isVisible());
            enter(root, "view nope");
            assertEquals(BOB, list(root).getSelectionModel().getSelectedItem());
            assertTrue(selectedCard(root).lookup("#details").isVisible());
            enter(root, "list");
            assertFalse(selectedCard(root).lookup("#details").isVisible());

            enter(root, "add r/parent n/New Parent p/81234567 e/new@example.com a/1 Road");
            assertEquals("New Parent", ((Person) list(root).getSelectionModel().getSelectedItem()).getName().fullName);
            assertFalse(selectedCard(root).lookup("#details").isVisible());
            enter(root, "view 3");
            assertEquals("Note: None", label(selectedCard(root), "note").getText());
            assertFalse(selectedCard(root).lookup("#subject").isManaged());
            saveSnapshot(root, "view-parent");
            enter(root, "delete 3");
            assertTrue(list(root).getSelectionModel().isEmpty());
            enter(root, "view 1");
            saveSnapshot(root, "view-student");
            stage.close();
        });
    }

    @Test
    void cards_longDetailsAtSmallWindow_wrapWithinCard() throws Exception {
        runOnFxThread(() -> {
            Person contact = new PersonBuilder(ALICE)
                    .withName("A very long contact name with several words that should wrap across multiple lines")
                    .withNote("Practice algebra and geometry before the next lesson. ".repeat(3))
                    .build();
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(contact, GEORGE));
            Scene scene = new Scene(panel.getRoot(), 460, 300);
            scene.getStylesheets().add(getClass().getResource("/view/DarkTheme.css").toExternalForm());
            panel.completeCommand(CommandResult.expandContact("view", contact));
            panel.getRoot().resize(460, 300);
            layout(panel.getRoot());
            saveSnapshot(panel.getRoot(), "narrow-card");
            Node card = selectedCard(panel.getRoot());
            assertTrue(label(card, "name").getHeight() > 20, "Long name must wrap to multiple lines");
            assertTrue(label(card, "note").getHeight() > 20, "Long note must wrap to multiple lines");
            assertTrue(card.getBoundsInParent().getMaxX() <= list(panel.getRoot()).getWidth());
        });
    }

    /**
     * Saves the rendered UI for visual inspection without opening a window or adding a library.
     */
    private static void saveSnapshot(Region root, String name) {
        WritableImage snapshot = root.snapshot(null, null);
        BufferedImage output = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                output.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        try {
            Path directory = Path.of("build", "reports", "ui");
            Files.createDirectories(directory);
            ImageIO.write(output, "png", directory.resolve(name + ".png").toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Label label(Node root, String id) {
        return (Label) root.lookup("#" + id);
    }

    private static ListView<?> list(Node root) {
        return (ListView<?>) root.lookup("#personListView");
    }

    /**
     * Returns the rendered selected card, failing when selection has no corresponding cell.
     */
    private static Node selectedCard(Node root) {
        Node card = root.lookupAll(".list-cell").stream()
                .filter(node -> node instanceof ListCell<?> cell && cell.isSelected())
                .map(node -> ((ListCell<?>) node).getGraphic())
                .findFirst().orElse(null);
        assertNotNull(card);
        return card;
    }

    private static void enter(Region root, String command) {
        TextField commandField = (TextField) root.lookup("#commandTextField");
        commandField.setText(command);
        commandField.getOnAction().handle(new ActionEvent());
        layout(root);
    }

    private static void layout(Region root) {
        root.applyCss();
        root.layout();
    }

    /**
     * Runs assertions on the JavaFX thread and propagates failures to JUnit.
     */
    private static void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }
}
