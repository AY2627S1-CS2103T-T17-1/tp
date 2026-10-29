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
 * Represents a command that acts on a parent and a student, both identified by their indices in the currently
 * displayed list. Subclasses share the index and role checks, so {@code link} and {@code unlink} report the same
 * problems in the same way.
 */
public abstract class ParentStudentCommand extends Command {

    public static final String MESSAGE_INVALID_INDEX = Messages.MESSAGE_INVALID_DISPLAYED_INDEX;
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = Messages.MESSAGE_DISPLAYED_INDEX_OUT_OF_RANGE;
    public static final String MESSAGE_NOT_PARENT = "The contact at index %1$s is not a parent.";
    public static final String MESSAGE_NOT_STUDENT = "The contact at index %1$s is not a student.";

    protected final Index parentIndex;
    protected final Index studentIndex;

    /**
     * Creates a command that acts on the parent at {@code parentIndex} and the student at {@code studentIndex}.
     *
     * @param parentIndex Index of the parent in the currently displayed list.
     * @param studentIndex Index of the student in the currently displayed list.
     */
    protected ParentStudentCommand(Index parentIndex, Index studentIndex) {
        requireAllNonNull(parentIndex, studentIndex);
        this.parentIndex = parentIndex;
        this.studentIndex = studentIndex;
    }

    /**
     * Returns the parent and student at this command's indices in the displayed list.
     *
     * @param model Model whose displayed list the indices refer to.
     * @param sameIndexMessage Feedback to give if both indices refer to the same contact.
     * @return The parent and student to act on.
     * @throws CommandException If either index is out of range, both indices are the same, or the contacts do not
     *     have the parent and student roles respectively. These are checked in that order.
     */
    protected ParentAndStudent getParentAndStudent(Model model, String sameIndexMessage) throws CommandException {
        requireNonNull(model);
        List<Person> displayedContacts = model.getFilteredPersonList();

        // Resolve both contacts first, so an index outside the list is reported even when both indices match.
        Person parent = getDisplayedContact(displayedContacts, parentIndex);
        Person student = getDisplayedContact(displayedContacts, studentIndex);
        if (parentIndex.equals(studentIndex)) {
            throw new CommandException(sameIndexMessage);
        }
        if (parent.getRole() != Role.PARENT) {
            throw new CommandException(String.format(MESSAGE_NOT_PARENT, parentIndex.getOneBased()));
        }
        if (student.getRole() != Role.STUDENT) {
            throw new CommandException(String.format(MESSAGE_NOT_STUDENT, studentIndex.getOneBased()));
        }
        return new ParentAndStudent(parent, student);
    }

    /**
     * Returns {@code Name (phone)}, so that messages distinguish contacts with the same name.
     */
    protected static String describe(Person person) {
        return person.getName().fullName + " (" + person.getPhone().value + ")";
    }

    private static Person getDisplayedContact(List<Person> displayedContacts, Index index) throws CommandException {
        if (index.getZeroBased() >= displayedContacts.size()) {
            throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, index.getOneBased()));
        }
        return displayedContacts.get(index.getZeroBased());
    }

    /**
     * Returns true if {@code other} is the same kind of command with the same indices.
     * A {@code link} and an {@code unlink} with the same indices are therefore not equal.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        ParentStudentCommand otherCommand = (ParentStudentCommand) other;
        return parentIndex.equals(otherCommand.parentIndex)
                && studentIndex.equals(otherCommand.studentIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("parentIndex", parentIndex)
                .add("studentIndex", studentIndex)
                .toString();
    }

    /**
     * Holds the parent and student that a command acts on.
     */
    protected record ParentAndStudent(Person parent, Person student) {

        /**
         * Returns the link between this parent and student.
         */
        ParentStudentLink toLink() {
            return ParentStudentLink.between(parent, student);
        }
    }
}
