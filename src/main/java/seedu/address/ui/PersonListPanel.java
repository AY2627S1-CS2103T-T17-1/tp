package seedu.address.ui;

import java.util.List;
import java.util.function.Function;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;

/**
 * Coordinates compact contact cards and a detail pane using one contact-based selection.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private static final double HORIZONTAL_MIN_WIDTH = 800;
    private static final double DEFAULT_DIVIDER_POSITION = 0.4;
    private final ContactListState state = new ContactListState();
    private final ContactDetailPanel detailPanel = new ContactDetailPanel();

    /**
     * Looks up relationships independently of the filtered list and of JavaFX cell reuse.
     */
    private final Function<Person, List<Person>> linkedPersonsLookup;

    /**
     * Ignores transient JavaFX selection changes while commands mutate or restore the list.
     */
    private boolean isExecutingCommand;
    private boolean isUpdatingSelection;

    @FXML
    private ListView<Person> personListView;
    @FXML
    private SplitPane contactSplitPane;
    @FXML
    private VBox contactsPane;
    @FXML
    private StackPane detailPanePlaceholder;
    @FXML
    private Label contactCount;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList} and no linked contacts.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        this(personList, person -> List.of());
    }

    /**
     * Creates a workspace whose detail pane includes the contacts returned by {@code linkedPersonsLookup}.
     *
     * @param personList Contacts to display.
     * @param linkedPersonsLookup Returns the contacts linked to a given contact.
     */
    public PersonListPanel(ObservableList<Person> personList, Function<Person, List<Person>> linkedPersonsLookup) {
        super(FXML);
        this.linkedPersonsLookup = linkedPersonsLookup;
        detailPanePlaceholder.getChildren().add(detailPanel.getRoot());
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!isExecutingCommand && !isUpdatingSelection) {
                state.select(newValue);
                refreshDetails();
            }
        });
        contactSplitPane.widthProperty().addListener((observable, oldWidth, newWidth) -> updateOrientation());
        updateOrientation();
        refreshDetails();
    }

    /**
     * Preserves the current selection while a command changes the backing list.
     */
    public void beginCommand() {
        isExecutingCommand = true;
    }

    /**
     * Applies a successful command's presentation request after execution and persistence finish.
     *
     * @param result Successfully completed command result.
     */
    public void completeCommand(CommandResult result) {
        state.apply(result, personListView.getItems());
        refreshSelection();
        if (state.getSelectedContact().isPresent()) {
            personListView.scrollTo(state.getSelectedContact().get());
        }
        isExecutingCommand = false;
    }

    /**
     * Restores presentation after a failed command, using the list restored by the logic layer.
     */
    public void cancelCommand() {
        state.retainVisibleContacts(personListView.getItems());
        refreshSelection();
        isExecutingCommand = false;
    }

    /**
     * Reconciles JavaFX selection with the contact retained independently of list indices.
     */
    private void refreshSelection() {
        isUpdatingSelection = true;
        if (state.getSelectedContact().isPresent()) {
            personListView.getSelectionModel().select(state.getSelectedContact().get());
        } else {
            personListView.getSelectionModel().clearSelection();
        }
        personListView.refresh();
        refreshDetails();
        isUpdatingSelection = false;
    }

    /**
     * Refreshes count and relationships after every command, including changes that do not alter the list.
     */
    private void refreshDetails() {
        int count = personListView.getItems().size();
        contactCount.setText(count + (count == 1 ? " contact" : " contacts"));
        if (state.getSelectedContact().isPresent()) {
            Person contact = state.getSelectedContact().orElseThrow();
            int displayedIndex = personListView.getItems().indexOf(contact) + 1;
            assert displayedIndex > 0 : "The selected contact must belong to the final displayed list";
            detailPanel.showContact(contact, displayedIndex, linkedPersonsLookup.apply(contact));
        } else {
            detailPanel.clear(count == 0);
        }
    }

    /**
     * Stacks panes when two readable columns no longer fit, without creating another window.
     */
    private void updateOrientation() {
        boolean isHorizontal = contactSplitPane.getWidth() >= HORIZONTAL_MIN_WIDTH;
        Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        contactsPane.setMinWidth(isHorizontal ? 280 : 0);
        detailPanePlaceholder.setMinWidth(isHorizontal ? 320 : 0);
        contactsPane.setMinHeight(isHorizontal ? 0 : 60);
        detailPanePlaceholder.setMinHeight(isHorizontal ? 0 : 40);
        if (contactSplitPane.getOrientation() != orientation) {
            contactSplitPane.setOrientation(orientation);
            contactSplitPane.setDividerPositions(DEFAULT_DIVIDER_POSITION);
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                PersonCard card = new PersonCard(person, getIndex() + 1);
                card.getRoot().prefWidthProperty().bind(widthProperty().subtract(20));
                setGraphic(card.getRoot());
            }
        }
    }

}
