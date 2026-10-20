package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Displays the TutorContacts command reference and User Guide link.
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2627s1-cs2103t-t17-1.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Full User Guide: " + USERGUIDE_URL;

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";
    private static final double INITIAL_SCREEN_FRACTION = 0.9;

    @FXML
    private Label helpMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        // JavaFX screen bounds account for display scaling and space occupied by system UI.
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        root.setWidth(Math.min(root.getWidth(), screenBounds.getWidth() * INITIAL_SCREEN_FRACTION));
        root.setHeight(Math.min(root.getHeight(), screenBounds.getHeight() * INITIAL_SCREEN_FRACTION));
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows and centers the command reference.
     *
     * @throws IllegalStateException If called outside the JavaFX Application Thread or during layout or animation.
     */
    public void show() {
        logger.info("Showing TutorContacts command guide.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     *
     * @return Whether the command reference is open.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        logger.fine("Focusing the existing TutorContacts command guide.");
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
        logger.fine("Copied the TutorContacts User Guide URL.");
    }
}
