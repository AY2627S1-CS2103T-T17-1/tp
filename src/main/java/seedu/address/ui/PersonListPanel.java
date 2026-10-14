package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final ContactListState state = new ContactListState();

    /**
     * Ignores transient JavaFX selection changes while commands mutate or restore the list.
     */
    private boolean isExecutingCommand;
    private boolean isUpdatingSelection;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!isExecutingCommand && !isUpdatingSelection) {
                state.select(newValue);
                personListView.refresh();
            }
        });
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
        if (result.getContact().isPresent()) {
            personListView.scrollTo(result.getContact().get());
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
        isUpdatingSelection = false;
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
                PersonCard card = new PersonCard(person, getIndex() + 1, state.isExpanded(person));
                card.getRoot().prefWidthProperty().bind(widthProperty().subtract(20));
                setGraphic(card.getRoot());
            }
        }
    }

}
