package seedu.address.ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;

/**
 * Displays a selected contact's complete information and existing parent-student relationships.
 */
public class ContactDetailPanel extends UiPart<Region> {
    private static final String FXML = "ContactDetailPanel.fxml";

    /**
     * Reflows paired fields before long values become difficult to read.
     */
    private static final double PAIRED_FIELDS_MIN_WIDTH = 480;
    // Keep these layout measurements aligned with ContactWorkspace.css and ContactDetailPanel.fxml.
    private static final double CONTENT_PADDING = 24;
    private static final double TUITION_PADDING = 16;
    private static final double FIELD_GAP = 24;

    private Person displayedContact;

    @FXML
    private ScrollPane detailScrollPane;
    @FXML
    private VBox details;
    @FXML
    private VBox emptyDetails;
    @FXML
    private Label emptyTitle;
    @FXML
    private Label emptyHint;
    @FXML
    private Label role;
    @FXML
    private Label contactIndex;
    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label address;
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
    @FXML
    private FlowPane contactFields;
    @FXML
    private FlowPane tuitionFields;

    /**
     * Creates a scrollable detail pane with an initial selection prompt.
     */
    public ContactDetailPanel() {
        super(FXML);
        detailScrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            double availableWidth = Math.max(0, newBounds.getWidth() - 2 * CONTENT_PADDING);
            boolean hasPairedFields = newBounds.getWidth() >= PAIRED_FIELDS_MIN_WIDTH;
            resizeFields(contactFields, availableWidth, hasPairedFields);
            resizeFields(tuitionFields, Math.max(0, availableWidth - 2 * TUITION_PADDING), hasPairedFields);
        });
        clear(false);
    }

    /**
     * Shows all applicable fields and refreshes relationships even when the selected person is unchanged.
     *
     * @param person Contact to display.
     * @param displayedIndex One-based index in the current displayed list.
     * @param linkedPersons Related contacts, including those outside the displayed list.
     */
    public void showContact(Person person, int displayedIndex, List<Person> linkedPersons) {
        boolean hasChangedContact = !person.equals(displayedContact);
        displayedContact = person;
        setShown(details, true);
        setShown(emptyDetails, false);
        showIdentity(person, displayedIndex);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        address.setText(person.getAddress().value);
        subject.setText(person.getSubject().map(Object::toString).orElse("Not recorded"));
        level.setText(person.getLevel().map(Object::toString).orElse("Not recorded"));
        setShown(tuitionFields, person.getRole() == Role.STUDENT);
        note.setText(person.getNote().map(Object::toString).orElse("None"));
        showRelationships(person, linkedPersons);
        tags.getChildren().clear();
        person.getTags().stream().sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        setShown(tags, !person.getTags().isEmpty());
        if (hasChangedContact) {
            detailScrollPane.setVvalue(0);
        }
    }

    /**
     * Clears obsolete details and shows guidance appropriate to the displayed list.
     *
     * @param isListEmpty Whether the current displayed list contains no contacts.
     */
    public void clear(boolean isListEmpty) {
        displayedContact = null;
        setShown(details, false);
        setShown(emptyDetails, true);
        emptyTitle.setText(isListEmpty ? "No contact selected" : "Select a contact");
        emptyHint.setText(isListEmpty ? "Type list to show all contacts, or add a contact."
                : "Type view INDEX or select a contact from the list.");
        detailScrollPane.setVvalue(0);
    }

    /**
     * Shows the selected contact's role, name, and current displayed index.
     *
     * @param person Contact whose identity to show.
     * @param displayedIndex Current one-based index.
     */
    private void showIdentity(Person person, int displayedIndex) {
        boolean isStudent = person.getRole() == Role.STUDENT;
        role.setText(isStudent ? "Student" : "Parent");
        role.getStyleClass().removeAll("student-role", "parent-role");
        role.getStyleClass().add(isStudent ? "student-role" : "parent-role");
        contactIndex.setText("CONTACT " + displayedIndex);
        name.setText(person.getName().fullName);
    }

    /**
     * Shows related contacts without inventing displayed indices for relatives outside the current filter.
     *
     * @param person Selected contact.
     * @param linkedPersons Related contacts resolved from the complete address book.
     */
    private void showRelationships(Person person, List<Person> linkedPersons) {
        String relatedContacts = linkedPersons.isEmpty() ? "None" : linkedPersons.stream()
                .map(contact -> contact.getName().fullName + " (" + contact.getPhone().value + ")")
                .collect(Collectors.joining(", "));
        links.setText((person.getRole() == Role.STUDENT ? "Linked parents: " : "Linked students: ") + relatedContacts);
    }

    /**
     * Keeps field containers aligned while letting FlowPane wrap complete label/value pairs.
     *
     * @param fields Group of label/value containers.
     * @param availableWidth Content width after padding.
     * @param hasPairedFields Whether two field containers should fit side by side.
     */
    private static void resizeFields(FlowPane fields, double availableWidth, boolean hasPairedFields) {
        double width = hasPairedFields ? Math.max(0, Math.floor((availableWidth - FIELD_GAP) / 2)) : availableWidth;
        for (Node field : fields.getChildren()) {
            Region fieldContainer = (Region) field;
            fieldContainer.setPrefWidth(width);
        }
    }

    /**
     * Removes hidden content from layout as well as from the rendered pane.
     *
     * @param region Region whose presence to update.
     * @param isShown Whether the region should participate in display and layout.
     */
    private static void setShown(Region region, boolean isShown) {
        region.setVisible(isShown);
        region.setManaged(isShown);
    }
}
