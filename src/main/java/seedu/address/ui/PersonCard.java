package seedu.address.ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private VBox details;
    @FXML
    private Label role;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label subject;
    @FXML
    private Label level;
    @FXML
    private Label note;
    @FXML
    private Label links;
    @FXML
    private FlowPane tags;

    /**
     * Creates a compact {@code PersonCard} with the given {@code Person} and index to display.
     *
     * @param person Contact to display.
     * @param displayedIndex One-based index in the current list.
     */
    public PersonCard(Person person, int displayedIndex) {
        this(person, displayedIndex, false);
    }

    /**
     * Creates a compact or expanded card with role-specific contact details.
     *
     * @param person Contact to display.
     * @param displayedIndex One-based index in the current list.
     * @param isExpanded Whether to show the contact's complete details.
     */
    public PersonCard(Person person, int displayedIndex, boolean isExpanded) {
        this(person, displayedIndex, isExpanded, List.of());
    }

    /**
     * Creates a compact or expanded card that also lists the contact's linked students or parents.
     *
     * @param person Contact to display.
     * @param displayedIndex One-based index in the current list.
     * @param isExpanded Whether to show the contact's complete details.
     * @param linkedPersons Students linked to a parent, or parents linked to a student.
     */
    public PersonCard(Person person, int displayedIndex, boolean isExpanded, List<Person> linkedPersons) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ".");
        name.setText(person.getName().fullName);
        boolean isStudent = person.getRole() == Role.STUDENT;
        role.setText(isStudent ? "Student" : "Parent");
        role.getStyleClass().add(isStudent ? "student-role" : "parent-role");
        phone.setText(person.getPhone().value);
        address.setText("Address: " + person.getAddress().value);
        email.setText("Email: " + person.getEmail().value);
        subject.setText("Subject: " + person.getSubject().map(Object::toString).orElse("Not recorded"));
        level.setText("Academic level: " + person.getLevel().map(Object::toString).orElse("Not recorded"));
        note.setText("Note: " + person.getNote().map(Object::toString).orElse("None"));
        links.setText((isStudent ? "Linked parents: " : "Linked students: ") + formatNames(linkedPersons));
        subject.setVisible(isStudent);
        subject.setManaged(isStudent);
        level.setVisible(isStudent);
        level.setManaged(isStudent);
        details.setVisible(isExpanded);
        details.setManaged(isExpanded);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        tags.setVisible(!person.getTags().isEmpty());
        tags.setManaged(!person.getTags().isEmpty());
    }

    private static String formatNames(List<Person> persons) {
        if (persons.isEmpty()) {
            return "None";
        }
        return persons.stream().map(person -> person.getName().fullName).collect(Collectors.joining(", "));
    }
}
