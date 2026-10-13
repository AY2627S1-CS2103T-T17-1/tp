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
        NONE, SELECT, EXPAND, COLLAPSE
    }

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    private final ContactListAction contactListAction;
    private final Optional<Person> contact;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, ContactListAction.NONE, Optional.empty());
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit,
            ContactListAction contactListAction, Optional<Person> contact) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.contactListAction = requireNonNull(contactListAction);
        this.contact = requireNonNull(contact);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Creates a result that selects a compact contact card after successful execution.
     *
     * @param feedbackToUser Feedback displayed to the user.
     * @param contact Contact to select in the displayed list.
     * @return Result requesting selection without expansion.
     */
    public static CommandResult selectContact(String feedbackToUser, Person contact) {
        return new CommandResult(feedbackToUser, false, false, ContactListAction.SELECT, Optional.of(contact));
    }

    /**
     * Creates a result that selects and expands a contact card after successful execution.
     *
     * @param feedbackToUser Feedback displayed to the user.
     * @param contact Contact whose details should be shown.
     * @return Result requesting selection and expansion.
     */
    public static CommandResult expandContact(String feedbackToUser, Person contact) {
        return new CommandResult(feedbackToUser, false, false, ContactListAction.EXPAND, Optional.of(contact));
    }

    /**
     * Creates a result that collapses contact details while retaining any visible selection.
     *
     * @param feedbackToUser Feedback displayed to the user.
     * @return Result requesting compact cards.
     */
    public static CommandResult collapseContacts(String feedbackToUser) {
        return new CommandResult(feedbackToUser, false, false, ContactListAction.COLLAPSE, Optional.empty());
    }

    public ContactListAction getContactListAction() {
        return contactListAction;
    }

    public Optional<Person> getContact() {
        return contact;
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
                && contact.equals(otherCommandResult.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, contactListAction, contact);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("contactListAction", contactListAction)
                .add("contact", contact)
                .toString();
    }

}
