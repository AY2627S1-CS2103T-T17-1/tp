package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;

/**
 * Links a parent to a student, both identified by their indices in the currently displayed list.
 */
public class LinkCommand extends Command {

    public static final String COMMAND_WORD = "link";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " PARENT_INDEX STUDENT_INDEX";
    public static final String MESSAGE_LINK_SUCCESS = "Linked parent %1$s to student %2$s.";
    public static final String MESSAGE_INVALID_FORMAT =
            "Invalid command format. Expected: " + MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_INDEX = Messages.MESSAGE_INVALID_DISPLAYED_INDEX;
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = Messages.MESSAGE_DISPLAYED_INDEX_OUT_OF_RANGE;
    public static final String MESSAGE_SAME_INDEX = "A contact cannot be linked to itself.";
    public static final String MESSAGE_NOT_PARENT = "The contact at index %1$s is not a parent.";
    public static final String MESSAGE_NOT_STUDENT = "The contact at index %1$s is not a student.";
    public static final String MESSAGE_DUPLICATE_LINK = "%1$s is already linked to %2$s.";

    private final Index parentIndex;
    private final Index studentIndex;

    /**
     * Creates a command to link the parent at {@code parentIndex} to the student at {@code studentIndex}.
     *
     * @param parentIndex Index of the parent in the currently displayed list.
     * @param studentIndex Index of the student in the currently displayed list.
     */
    public LinkCommand(Index parentIndex, Index studentIndex) {
        requireAllNonNull(parentIndex, studentIndex);
        this.parentIndex = parentIndex;
        this.studentIndex = studentIndex;
    }

    /**
     * Links the two displayed contacts and selects the parent so the user can see the new link in its details.
     * Contact data is left unchanged if any check fails.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedContacts = model.getFilteredPersonList();

        Person parent = getDisplayedContact(displayedContacts, parentIndex);
        Person student = getDisplayedContact(displayedContacts, studentIndex);
        if (parentIndex.equals(studentIndex)) {
            throw new CommandException(MESSAGE_SAME_INDEX);
        }
        if (parent.getRole() != Role.PARENT) {
            throw new CommandException(String.format(MESSAGE_NOT_PARENT, parentIndex.getOneBased()));
        }
        if (student.getRole() != Role.STUDENT) {
            throw new CommandException(String.format(MESSAGE_NOT_STUDENT, studentIndex.getOneBased()));
        }

        ParentStudentLink link = ParentStudentLink.between(parent, student);
        if (model.hasLink(link)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_LINK, describe(parent), describe(student)));
        }

        model.addLink(link);
        return CommandResult.selectContact(String.format(MESSAGE_LINK_SUCCESS, describe(parent), describe(student)),
                parent);
    }

    /**
     * Returns {@code Name (phone)}, so that messages distinguish contacts with the same name.
     */
    private static String describe(Person person) {
        return person.getName().fullName + " (" + person.getPhone().value + ")";
    }

    private static Person getDisplayedContact(List<Person> displayedContacts, Index index) throws CommandException {
        if (index.getZeroBased() >= displayedContacts.size()) {
            throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, index.getOneBased()));
        }
        return displayedContacts.get(index.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof LinkCommand otherLinkCommand)) {
            return false;
        }

        return parentIndex.equals(otherLinkCommand.parentIndex)
                && studentIndex.equals(otherLinkCommand.studentIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("parentIndex", parentIndex)
                .add("studentIndex", studentIndex)
                .toString();
    }
}
