package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
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
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.address.MainApp;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.SortCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises FXML cards, Help, and command-to-UI wiring on a desktop with {@code RUN_UI_TESTS=true}.
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

    /**
     * Checks command-box filtering, card numbering, selection, expansion, and list restoration.
     *
     * @throws Exception If JavaFX execution, command handling, or saving the snapshot fails.
     */
    @Test
    void window_filterCommands_updateOrderIndicesAndSelection() throws Exception {
        runOnFxThread(() -> {
            Person zoe = new PersonBuilder().withName("Zoe Tan").build();
            Person amy = new PersonBuilder().withName("Amy Tan").build();
            Person parent = new PersonBuilder().withName("Alice Tan").withRole("parent").build();
            Model model = new ModelManager();
            List.of(zoe, parent, amy).forEach(model::addPerson);
            Path dataPath = temporaryFolder.resolve("contacts.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(model, storage), dataPath);
            window.fillInnerParts();
            Region root = (Region) stage.getScene().getRoot();
            root.resize(1000, 620);
            try {
                enter(root, "filter s/Mathematics l/Secondary 3");
                assertEquals(List.of(amy, zoe), list(root).getItems());
                assertEquals("Showing 2 matching contacts.", ((TextArea) root.lookup("#resultDisplay")).getText());
                enter(root, "view 2");
                enter(root, "filter s/Mathematics");
                assertEquals(zoe, list(root).getSelectionModel().getSelectedItem());
                assertEquals("2.", label(selectedCard(root), "id").getText());
                assertTrue(selectedCard(root).lookup("#details").isVisible());
                saveSnapshot(root, "filter-name-order");

                enter(root, "filter n/");
                assertEquals(zoe, list(root).getSelectionModel().getSelectedItem());
                assertTrue(selectedCard(root).lookup("#details").isVisible());
                enter(root, "filter n/Amy Tan");
                assertEquals(List.of(amy), list(root).getItems());
                assertTrue(list(root).getSelectionModel().isEmpty());
                enter(root, "filter n/Amy Zoe");
                assertTrue(list(root).getItems().isEmpty());
                assertEquals("No matching contacts.", ((TextArea) root.lookup("#resultDisplay")).getText());

                enter(root, "filter s/Mathematics");
                enter(root, "delete 1");
                assertEquals(List.of(zoe), list(root).getItems());
                enter(root, "view 1");
                assertEquals(zoe, list(root).getSelectionModel().getSelectedItem());
                assertEquals("1.", label(selectedCard(root), "id").getText());
                enter(root, "list");
                assertEquals(List.of(zoe, parent), list(root).getItems());
                assertFalse(selectedCard(root).lookup("#details").isVisible());
            } finally {
                stage.close();
            }
        });
    }

    @Test
    void helpWindow_commandReference_showsSupportedCommandsAndCopiesTutorContactsUrl() throws Exception {
        runOnFxThread(() -> {
            HelpWindow help = new HelpWindow();
            Region root = (Region) help.getRoot().getScene().getRoot();
            layout(root);
            assertEquals("https://ay2627s1-cs2103t-t17-1.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
            assertTrue(label(root, "helpMessage").getText().contains(HelpWindow.USERGUIDE_URL));
            Set<String> commands = new HashSet<>();
            for (Node syntax : root.lookupAll(".command-syntax")) {
                commands.add(((Label) syntax).getText().split(" ")[0]);
            }
            assertEquals(Set.of("add", "list", "sort", "view", "edit", "find", "filter", "delete", "clear", "help",
                    "exit"), commands);

            Clipboard clipboard = Clipboard.getSystemClipboard();
            Map<DataFormat, Object> previousContent = new HashMap<>();
            for (DataFormat format : clipboard.getContentTypes()) {
                // Native formats can be advertised even when JavaFX cannot retrieve their content.
                Object content = clipboard.getContent(format);
                if (content != null) {
                    previousContent.put(format, content);
                }
            }
            try {
                Button copyButton = (Button) root.lookup("#copyButton");
                copyButton.fire();
                assertEquals(HelpWindow.USERGUIDE_URL, clipboard.getString());
            } finally {
                clipboard.setContent(previousContent);
            }
        });
    }

    @Test
    void helpWindow_labels_usePreferredMinimumHeight() throws Exception {
        runOnFxThread(() -> {
            HelpWindow help = new HelpWindow();
            Region root = (Region) help.getRoot().getScene().getRoot();
            layout(root);
            Set<Node> labels = root.lookupAll(".label");
            assertFalse(labels.isEmpty());
            for (Node node : labels) {
                Label label = (Label) node;
                assertEquals(Region.USE_PREF_SIZE, label.getMinHeight(), label.getText());
                assertEquals(label.prefHeight(label.getWidth()), label.minHeight(label.getWidth()), label.getText());
            }
        });
    }

    /**
     * Checks the minimum-size Help layout and that scrolling can reveal the exit command without clipping.
     *
     * @throws Exception If JavaFX execution or layout synchronization fails.
     */
    @Test
    void helpWindow_smallSize_keepsGuideScrollableAndFooterVisible() throws Exception {
        HelpWindow help = callOnFxThread(HelpWindow::new);
        Stage stage = callOnFxThread(help::getRoot);
        Scene scene = callOnFxThread(stage::getScene);
        try {
            runOnFxThread(help::show);
            waitForLayout(scene);
            runOnFxThread(() -> {
                Region root = (Region) stage.getScene().getRoot();
                saveSnapshot(root, "help-command-guide");
                stage.setWidth(450);
                stage.setHeight(360);
            });
            waitForLayout(scene);
            runOnFxThread(() -> {
                Region root = (Region) scene.getRoot();
                assertTrue(scene.getWidth() > 0);
                assertTrue(scene.getHeight() > 0);
                assertEquals(scene.getWidth(), root.getWidth(), 1);
                assertEquals(scene.getHeight(), root.getHeight(), 1);
                assertEquals(stage.getMinWidth(), stage.getWidth(), 1);
                assertEquals(stage.getMinHeight(), stage.getHeight(), 1);
                ScrollPane scrollPane = (ScrollPane) root.lookup("#commandScrollPane");
                assertEquals(450, stage.getMinWidth());
                assertEquals(360, stage.getMinHeight());
                assertTrue(stage.isResizable());
                assertTrue(scrollPane.isFitToWidth());
                assertTrue(scrollPane.getContent().getBoundsInLocal().getHeight()
                        > scrollPane.getViewportBounds().getHeight());
                assertTrue(scrollPane.getContent().getBoundsInLocal().getWidth()
                        <= scrollPane.getViewportBounds().getWidth() + 1);
                assertVisibleWithinWindow(root, scrollPane);
                assertVisibleWithinWindow(root, root.lookup("#helpMessage"));
                assertVisibleWithinWindow(root, root.lookup("#copyButton"));
                saveSnapshot(root, "help-command-guide-small");
                // The legend below Exit can fill the viewport, so reveal Exit using its actual position.
                Node exitSyntax = root.lookupAll(".command-syntax").stream()
                        .filter(node -> ((Label) node).getText().equals("exit"))
                        .findFirst().orElseThrow();
                Bounds exitBounds = scrollPane.getContent().sceneToLocal(
                        exitSyntax.localToScene(exitSyntax.getBoundsInLocal()));
                double viewportHeight = scrollPane.getViewportBounds().getHeight();
                double scrollableHeight = scrollPane.getContent().getLayoutBounds().getHeight() - viewportHeight;
                double scrollOffset = exitBounds.getMinY() - (viewportHeight - exitBounds.getHeight()) / 2;
                scrollPane.setVvalue(Math.max(0, Math.min(1, scrollOffset / scrollableHeight)));
            });
            waitForLayout(scene);
            runOnFxThread(() -> {
                Region root = (Region) scene.getRoot();
                ScrollPane scrollPane = (ScrollPane) root.lookup("#commandScrollPane");
                Node exitSyntax = root.lookupAll(".command-syntax").stream()
                        .filter(node -> ((Label) node).getText().equals("exit"))
                        .findFirst().orElseThrow();
                Node viewport = scrollPane.lookup(".viewport");
                Bounds exitBounds = exitSyntax.localToScene(exitSyntax.getBoundsInLocal());
                Bounds viewportBounds = viewport.localToScene(viewport.getBoundsInLocal());
                assertTrue(exitBounds.getMinY() >= viewportBounds.getMinY() - 1,
                        "Exit must not be clipped above the viewport");
                assertTrue(exitBounds.getMaxY() <= viewportBounds.getMaxY() + 1,
                        "Exit must not be clipped below the viewport");
            });
        } finally {
            runOnFxThread(help::hide);
        }
    }

    @Test
    void window_helpCommandAndMenu_reuseHelpWindowAndKeepContactsUnchanged() throws Exception {
        runOnFxThread(() -> {
            Model model = new ModelManager();
            model.addPerson(ALICE);
            Path dataPath = temporaryFolder.resolve("contacts.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(model, storage), dataPath);
            window.fillInnerParts();
            Region root = (Region) stage.getScene().getRoot();
            List<Window> existingWindows = List.copyOf(Window.getWindows());
            try {
                enter(root, "help");
                Stage helpStage = (Stage) Window.getWindows().stream()
                        .filter(candidate -> candidate instanceof Stage candidateStage
                                && candidateStage.getTitle().equals("TutorContacts — Command guide"))
                        .findFirst().orElseThrow();
                int windowCount = Window.getWindows().size();
                enter(root, "help");
                assertEquals(windowCount, Window.getWindows().size());

                MenuBar menuBar = (MenuBar) root.lookup("#menuBar");
                MenuItem helpMenu = menuBar.getMenus().get(1).getItems().getFirst();
                assertEquals(KeyCombination.valueOf("F1"), helpMenu.getAccelerator());
                helpMenu.fire();
                assertTrue(helpStage.isShowing());
                assertEquals(windowCount, Window.getWindows().size());
                assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
                assertFalse(Files.exists(dataPath));
                helpStage.hide();
                helpMenu.fire();
                assertTrue(helpStage.isShowing());
                helpStage.hide();
                TextField commandField = (TextField) root.lookup("#commandTextField");
                commandField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                        false, false, false, false));
                assertTrue(helpStage.isShowing());
                helpStage.hide();
            } finally {
                try {
                    closeNewWindows(existingWindows);
                } finally {
                    stage.close();
                }
            }
        });
    }

    @Test
    void closeNewWindows_assertionFailure_hidesHelpWindow() throws Exception {
        runOnFxThread(() -> {
            List<Window> existingWindows = List.copyOf(Window.getWindows());
            HelpWindow help = new HelpWindow();
            AssertionError failure = assertThrows(AssertionError.class, () -> {
                try {
                    help.show();
                    assertTrue(help.isShowing());
                    throw new AssertionError("Simulated failure before capturing the Help stage");
                } finally {
                    closeNewWindows(existingWindows);
                }
            });
            assertEquals("Simulated failure before capturing the Help stage", failure.getMessage());
            assertFalse(help.isShowing());
            assertEquals(existingWindows, List.copyOf(Window.getWindows()));
        });
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
    void window_prefixErrorsAndEscapes_preserveSelectionAndStoreLiteralText() throws Exception {
        Path dataPath = temporaryFolder.resolve("contacts.json");
        runOnFxThread(() -> {
            Model model = new ModelManager();
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(model, storage), dataPath);
            window.fillInnerParts();
            Region root = (Region) stage.getScene().getRoot();
            root.resize(800, 600);

            enter(root, "ADD R/PARENT N/Prefix Parent P/81234567 E/prefix@example.com A/1 Road");
            enter(root, "view 1");
            Person selectedContact = (Person) list(root).getSelectionModel().getSelectedItem();
            enter(root, "add n/Alex x/foo");
            assertEquals(1, model.getFilteredPersonList().size());
            assertEquals(selectedContact, list(root).getSelectionModel().getSelectedItem());
            assertTrue(selectedCard(root).lookup("#details").isVisible());
            enter(root, "add n/Alex N/Bob");
            assertEquals(selectedContact, list(root).getSelectionModel().getSelectedItem());
            assertTrue(selectedCard(root).lookup("#details").isVisible());

            enter(root, "add r/parent n/Literal Parent p/91234567 e/literal@example.com a/Block \\x/12");
            Person addedContact = (Person) list(root).getSelectionModel().getSelectedItem();
            assertEquals("Block x/12", addedContact.getAddress().value);
            assertEquals(2, model.getFilteredPersonList().size());
            stage.close();
        });
        assertEquals("Block x/12",
                new JsonAddressBookStorage(dataPath).readAddressBook().orElseThrow().getPersonList().get(1)
                        .getAddress().value);
    }

    @Test
    void window_mvpStudentAndParentFlow_showsAllFieldsAndRejectsInvalidInput() throws Exception {
        Path dataPath = temporaryFolder.resolve("contacts.json");
        runOnFxThread(() -> {
            Model model = new ModelManager();
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(model, storage), dataPath);
            window.fillInnerParts();
            Region root = (Region) stage.getScene().getRoot();
            root.resize(800, 600);

            enter(root, "ADD R/STUDENT N/Alex   Tan P/+9123-4567 E/Alex@Example.com A/1 Road"
                    + " S/A-Math L/Secondary   3 O/Discuss \\x/foo");
            enter(root, "view 1");
            Person student = (Person) list(root).getSelectionModel().getSelectedItem();
            assertEquals("Alex Tan", student.getName().fullName);
            assertEquals("Subject: A-Math", label(selectedCard(root), "subject").getText());
            assertEquals("Academic level: Secondary 3", label(selectedCard(root), "level").getText());
            assertEquals("Note: Discuss x/foo", label(selectedCard(root), "note").getText());

            enter(root, "add r/parent n/May Tan p/88888888 e/may@example.com a/2 Road s/");
            assertEquals(1, model.getFilteredPersonList().size());
            assertEquals(student, list(root).getSelectionModel().getSelectedItem());
            assertTrue(selectedCard(root).lookup("#details").isVisible());
            enter(root, "exit 3");
            assertEquals(student, list(root).getSelectionModel().getSelectedItem());

            enter(root, "add r/parent n/May Tan p/88888888 e/may@example.com a/2 Road o/");
            enter(root, "view 2");
            assertFalse(selectedCard(root).lookup("#subject").isManaged());
            assertFalse(selectedCard(root).lookup("#level").isManaged());
            assertEquals("Note: None", label(selectedCard(root), "note").getText());
            enter(root, "list");
            assertFalse(selectedCard(root).lookup("#details").isVisible());
            enter(root, "delete 2");
            assertEquals(1, model.getFilteredPersonList().size());
            stage.close();
        });
        Person restored = new JsonAddressBookStorage(dataPath).readAddressBook().orElseThrow()
                .getPersonList().getFirst();
        assertEquals("A-Math", restored.getSubject().orElseThrow().value);
        assertEquals("Secondary 3", restored.getLevel().orElseThrow().value);
        assertEquals("Discuss x/foo", restored.getNote().orElseThrow().value);
    }

    @Test
    void window_atMinimumHeight_keepsCommandAndContactListUsable() throws Exception {
        runOnFxThread(() -> {
            Model model = new ModelManager();
            model.addPerson(ALICE);
            Path dataPath = temporaryFolder.resolve("contacts.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(model, storage), dataPath);
            window.fillInnerParts();
            stage.setWidth(800);
            stage.setHeight(420);

            try {
                window.show();
                Region root = (Region) stage.getScene().getRoot();
                layout(root);
                assertEquals(420, stage.getMinHeight());
                assertTrue(root.getHeight() <= 420);
                assertVisibleWithinWindow(root, root.lookup("#commandTextField"));
                assertVisibleWithinWindow(root, root.lookup("#resultDisplay"));
                assertVisibleWithinWindow(root, list(root));
                assertVisibleWithinWindow(root, root.lookup("#saveLocationStatus"));

                enter(root, "view 1");
                assertEquals(ALICE, list(root).getSelectionModel().getSelectedItem());
                assertTrue(selectedCard(root).lookup("#details").isVisible());
                enter(root, "list");
                assertFalse(selectedCard(root).lookup("#details").isVisible());
                saveSnapshot(root, "minimum-height-window");
            } finally {
                stage.close();
            }
        });
    }

    @Test
    void uiManager_startWithDataWarning_displaysWarning() throws Exception {
        runOnFxThread(() -> {
            Path dataPath = temporaryFolder.resolve("contacts.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            UiManager uiManager = new UiManager(new LogicManager(new ModelManager(), storage), dataPath,
                    MainApp.DATA_FILE_WARNING);
            Stage stage = new Stage();

            try {
                uiManager.start(stage);
                TextArea feedback = (TextArea) stage.getScene().getRoot().lookup("#resultDisplay");
                assertNotNull(feedback);
                assertEquals(MainApp.DATA_FILE_WARNING, feedback.getText());
            } finally {
                stage.close();
            }
        });
    }

    @Test
    void cards_longDetailsAtSmallWindow_wrapWithinCard() throws Exception {
        runOnFxThread(() -> {
            Person contact = new PersonBuilder(ALICE)
                    .withName("A long contact name with several words that wraps across lines")
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
     * Checks that a control has usable height and stays inside the laid-out window.
     */
    private static void assertVisibleWithinWindow(Region root, Node control) {
        assertNotNull(control);
        assertTrue(control.isVisible());
        assertTrue(control.getBoundsInLocal().getHeight() > 0);
        assertTrue(control.localToScene(control.getBoundsInLocal()).getMinX() >= -1);
        assertTrue(control.localToScene(control.getBoundsInLocal()).getMaxX() <= root.getWidth() + 1);
        assertTrue(control.localToScene(control.getBoundsInLocal()).getMinY() >= -1);
        assertTrue(control.localToScene(control.getBoundsInLocal()).getMaxY() <= root.getHeight() + 1);
    }

    /**
     * Hides windows opened by a test, including Help when an assertion fails before it can be captured.
     */
    private static void closeNewWindows(List<Window> existingWindows) {
        for (Window window : List.copyOf(Window.getWindows())) {
            if (!existingWindows.contains(window)) {
                window.hide();
            }
        }
    }

    /**
     * Waits off the JavaFX thread for two post-layout pulses after showing, resizing, or scrolling a scene.
     */
    private static void waitForLayout(Scene scene) throws Exception {
        CountDownLatch pulses = new CountDownLatch(2);
        Runnable listener = () -> {
            pulses.countDown();
            if (pulses.getCount() > 0) {
                Platform.requestNextPulse();
            }
        };
        runOnFxThread(() -> {
            scene.addPostLayoutPulseListener(listener);
            Platform.requestNextPulse();
        });
        try {
            assertTrue(pulses.await(10, TimeUnit.SECONDS), "Timed out waiting for JavaFX layout");
        } finally {
            runOnFxThread(() -> scene.removePostLayoutPulseListener(listener));
        }
    }

    /**
     * Runs assertions on the JavaFX thread and propagates failures to JUnit.
     */
    private static void runOnFxThread(Runnable action) throws Exception {
        callOnFxThread(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Returns a value from the JavaFX thread and propagates failures to JUnit.
     */
    private static <T> T callOnFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(15, TimeUnit.SECONDS);
    }

    /**
     * Checks that an expanded card follows its contact when sorting changes the displayed index.
     */
    @Test
    void panel_sort_preservesExpandedContactAndUpdatesIndex() throws Exception {
        runOnFxThread(() -> {
            Person later = new PersonBuilder().withName("Zoe").withLevel("JC 2").build();
            Person earlier = new PersonBuilder().withName("Amy").withLevel("Primary 1").build();
            Model model = new ModelManager();
            model.addPerson(later);
            model.addPerson(earlier);
            PersonListPanel panel = new PersonListPanel(model.getFilteredPersonList());
            new Scene(panel.getRoot(), 760, 600);
            panel.completeCommand(CommandResult.expandContact("view", later));

            panel.beginCommand();
            panel.completeCommand(new SortCommand().execute(model));
            layout(panel.getRoot());
            assertEquals(later, list(panel.getRoot()).getSelectionModel().getSelectedItem());
            assertEquals("2.", label(selectedCard(panel.getRoot()), "id").getText());
            assertTrue(selectedCard(panel.getRoot()).lookup("#details").isVisible());

        });
    }
}
