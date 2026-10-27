package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentStudentLink;
import seedu.address.model.person.Person;

/**
 * Links a parent to a student, both identified by their indices in the currently displayed list.
 */
public class LinkCommand extends ParentStudentCommand {

    public static final String COMMAND_WORD = "link";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " PARENT_INDEX STUDENT_INDEX";
    public static final String MESSAGE_LINK_SUCCESS = "Linked parent %1$s to student %2$s.";
    public static final String MESSAGE_INVALID_FORMAT =
            "Invalid command format. Expected: " + MESSAGE_USAGE;
    public static final String MESSAGE_SAME_INDEX = "A contact cannot be linked to itself.";
    public static final String MESSAGE_DUPLICATE_LINK = "%1$s is already linked to %2$s.";

    /**
     * Creates a command to link the parent at {@code parentIndex} to the student at {@code studentIndex}.
     *
     * @param parentIndex Index of the parent in the currently displayed list.
     * @param studentIndex Index of the student in the currently displayed list.
     */
    public LinkCommand(Index parentIndex, Index studentIndex) {
        super(parentIndex, studentIndex);
    }

    /**
     * Links the two displayed contacts and expands the parent so the user can see the new link.
     * Contact data is left unchanged if any check fails.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ParentAndStudent contacts = getParentAndStudent(model, MESSAGE_SAME_INDEX);
        Person parent = contacts.parent();
        Person student = contacts.student();

        ParentStudentLink link = contacts.toLink();
        if (model.hasLink(link)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_LINK, describe(parent), describe(student)));
        }

        model.addLink(link);
        return CommandResult.expandContact(String.format(MESSAGE_LINK_SUCCESS, describe(parent), describe(student)),
                parent);
    }
}
