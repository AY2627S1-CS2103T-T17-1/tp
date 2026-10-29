package seedu.address.ui;

import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;

/**
 * Tracks the contact shown in the detail pane independently of shifting list indices and JavaFX cells.
 */
class ContactListState {
    private Person selectedContact;

    Optional<Person> getSelectedContact() {
        return Optional.ofNullable(selectedContact);
    }

    /**
     * Selects a contact, or clears the detail pane when the selection is empty.
     *
     * @param contact Selected contact, or null to clear selection.
     */
    void select(Person contact) {
        selectedContact = contact;
    }

    /**
     * Applies a successful command's presentation request to the final displayed list.
     *
     * @param result Presentation request from the successfully completed command.
     * @param visibleContacts Final displayed list after execution and any persistence.
     */
    void apply(CommandResult result, List<Person> visibleContacts) {
        switch (result.getContactListAction()) {
            case SELECT:
                selectedContact = result.getContact().orElseThrow();
                break;
            case REPLACE:
                if (result.getPreviousContact().orElseThrow().equals(selectedContact)) {
                    selectedContact = result.getContact().orElseThrow();
                }
                break;
            case NONE:
                break;
            default:
                throw new AssertionError("Unknown contact-list action");
        }
        retainVisibleContacts(visibleContacts);
    }

    /**
     * Clears selection if deletion or filtering removes the selected contact from view.
     *
     * @param visibleContacts Final visible contacts, including restored contacts after a failed command.
     */
    void retainVisibleContacts(List<Person> visibleContacts) {
        if (selectedContact != null && !visibleContacts.contains(selectedContact)) {
            selectedContact = null;
        }
    }
}
