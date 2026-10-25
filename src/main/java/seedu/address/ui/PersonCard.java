package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;

/**
 * Displays a compact indexed contact summary; complete information belongs to the detail pane.
 */
public class PersonCard extends UiPart<Region> {
    private static final String FXML = "PersonListCard.fxml";

    @FXML
    private Label role;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label summary;

    /**
     * Creates a compact card with a student tuition summary when applicable.
     *
     * @param person Contact to display.
     * @param displayedIndex One-based index in the current displayed list.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        id.setText(displayedIndex + ".");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        boolean isStudent = person.getRole() == Role.STUDENT;
        role.setText(isStudent ? "Student" : "Parent");
        role.getStyleClass().add(isStudent ? "student-role" : "parent-role");
        summary.setText(person.getLevel().map(Object::toString).orElse("Not recorded")
                + " · " + person.getSubject().map(Object::toString).orElse("Not recorded"));
        summary.setVisible(isStudent);
        summary.setManaged(isStudent);
    }
}
