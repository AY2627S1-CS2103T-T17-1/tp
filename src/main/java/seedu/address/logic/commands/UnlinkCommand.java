package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;

/**
 * Removes the link between a parent and a student, both identified by their indices in the currently displayed list.
 * Both contacts are kept.
 */
public class UnlinkCommand extends ParentStudentCommand {

    public static final String COMMAND_WORD = "unlink";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " PARENT_INDEX STUDENT_INDEX";
    public static final String MESSAGE_UNLINK_SUCCESS = "Unlinked parent %1$s from student %2$s.";
    public static final String MESSAGE_INVALID_FORMAT =
            "Invalid command format. Expected: " + MESSAGE_USAGE;
    public static final String MESSAGE_SAME_INDEX = "A contact cannot be unlinked from itself.";
    public static final String MESSAGE_LINK_NOT_FOUND = "%1$s is not linked to %2$s.";

    /**
     * Creates a command to unlink the parent at {@code parentIndex} from the student at {@code studentIndex}.
     *
     * @param parentIndex Index of the parent in the currently displayed list.
     * @param studentIndex Index of the student in the currently displayed list.
     */
    public UnlinkCommand(Index parentIndex, Index studentIndex) {
        super(parentIndex, studentIndex);
    }

    /**
     * Removes the link between the two displayed contacts and selects the parent so the user can see its remaining
     * links in its details.
     * Contact data is left unchanged if any check fails.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ParentAndStudent contacts = getParentAndStudent(model, MESSAGE_SAME_INDEX);
        Person parent = contacts.parent();
        Person student = contacts.student();

        ParentStudentLink link = contacts.toLink();
        if (!model.hasLink(link)) {
            throw new CommandException(String.format(MESSAGE_LINK_NOT_FOUND, describe(parent), describe(student)));
        }

        model.removeLink(link);
        return CommandResult.selectContact(
                String.format(MESSAGE_UNLINK_SUCCESS, describe(parent), describe(student)), parent);
    }
}
