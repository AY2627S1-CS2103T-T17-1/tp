package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    /**
     * Describes the contact-list presentation requested after a successful command.
     */
    public enum ContactListAction {
        NONE, SELECT, REPLACE
    }

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    private final ContactListAction contactListAction;
    private final Optional<Person> contact;
    private final Optional<Person> previousContact;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, ContactListAction.NONE, Optional.empty(), Optional.empty());
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit,
            ContactListAction contactListAction, Optional<Person> contact, Optional<Person> previousContact) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.contactListAction = requireNonNull(contactListAction);
        this.contact = requireNonNull(contact);
        this.previousContact = requireNonNull(previousContact);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Creates a result that selects a contact and shows its details after successful execution.
     *
     * @param feedbackToUser Feedback displayed to the user.
     * @param contact Contact to select in the displayed list.
     * @return Result requesting selection and details.
     */
    public static CommandResult selectContact(String feedbackToUser, Person contact) {
        return new CommandResult(feedbackToUser, false, false, ContactListAction.SELECT,
                Optional.of(contact), Optional.empty());
    }

    /**
     * Creates a result that follows an edited contact only when its previous version was selected.
     *
     * @param feedbackToUser Feedback displayed to the user.
     * @param previousContact Contact replaced by the edit.
     * @param contact Replacement contact in the final displayed list.
     * @return Result describing the replacement without selecting an unrelated contact.
     */
    public static CommandResult replaceContact(String feedbackToUser, Person previousContact, Person contact) {
        return new CommandResult(feedbackToUser, false, false, ContactListAction.REPLACE,
                Optional.of(contact), Optional.of(previousContact));
    }

    public ContactListAction getContactListAction() {
        return contactListAction;
    }

    public Optional<Person> getContact() {
        return contact;
    }

    /**
     * Returns the previous version of an edited contact, when this result describes a replacement.
     *
     * @return Previous contact for a replacement request, or an empty optional for other actions.
     */
    public Optional<Person> getPreviousContact() {
        return previousContact;
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && contactListAction == otherCommandResult.contactListAction
                && contact.equals(otherCommandResult.contact)
                && previousContact.equals(otherCommandResult.previousContact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, contactListAction, contact, previousContact);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("contactListAction", contactListAction)
                .add("contact", contact)
                .add("previousContact", previousContact)
                .toString();
    }

}
