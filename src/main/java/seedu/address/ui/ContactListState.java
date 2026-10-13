package seedu.address.ui;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;

/**
 * Tracks contact selection and expansion independently of shifting list indices and JavaFX cells.
 */
class ContactListState {
    private Person selectedContact;
    private Person expandedContact;

    Optional<Person> getSelectedContact() {
        return Optional.ofNullable(selectedContact);
    }

    boolean isExpanded(Person contact) {
        return contact.equals(expandedContact);
    }

    /**
     * Selects a contact, collapsing details when the user moves to a different card.
     */
    void select(Person contact) {
        if (!Objects.equals(selectedContact, contact)) {
            expandedContact = null;
        }
        selectedContact = contact;
    }

    /**
     * Applies a successful command's presentation request to the final displayed list.
     */
    void apply(CommandResult result, List<Person> visibleContacts) {
        switch (result.getContactListAction()) {
            case SELECT:
                selectedContact = result.getContact().orElseThrow();
                expandedContact = null;
                break;
            case EXPAND:
                selectedContact = result.getContact().orElseThrow();
                expandedContact = selectedContact;
                break;
            case COLLAPSE:
                expandedContact = null;
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
     */
    void retainVisibleContacts(List<Person> visibleContacts) {
        if (selectedContact != null && !visibleContacts.contains(selectedContact)) {
            selectedContact = null;
            expandedContact = null;
        }
    }
}
