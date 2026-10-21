package seedu.address.ui;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;

/**
 * A UI for the status bar that is displayed at the footer of the application.
 */
public class StatusBarFooter extends UiPart<Region> {

    private static final String FXML = "StatusBarFooter.fxml";

    @FXML
    private Label saveLocationStatus;

    /**
     * Creates a {@code StatusBarFooter} with the given {@code Path}.
     *
     * @param saveLocation Actual contact-data path to display, including in the full-path tooltip.
     */
    public StatusBarFooter(Path saveLocation) {
        super(FXML);
        saveLocationStatus.setText(Paths.get(".").resolve(saveLocation).toString());
        // Keep the complete path available when the footer label is shortened by the window width.
        Tooltip fullPath = new Tooltip(saveLocationStatus.getText());
        fullPath.setWrapText(true);
        fullPath.setMaxWidth(480);
        saveLocationStatus.setTooltip(fullPath);
    }

}
