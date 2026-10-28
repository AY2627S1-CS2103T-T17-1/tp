package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ContactMatchesCriteriaPredicate;
import seedu.address.model.person.Level;
import seedu.address.model.person.Subject;

/**
 * Parses optional name, subject, and academic-level criteria into a combined AND filter.
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    /**
     * Parses criteria, rejecting unknown prefixes, duplicates, invalid structure, and invalid values in that order.
     *
     * @param args Arguments following the command word.
     * @return A command containing at least one validated criterion.
     * @throws ParseException If the input violates the filter syntax or field constraints.
     */
    @Override
    public FilterCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments =
                ArgumentTokenizer.tokenizeForCommand(args, PREFIX_NAME, PREFIX_SUBJECT, PREFIX_LEVEL);
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_SUBJECT, PREFIX_LEVEL);
        Optional<String> nameValue = arguments.getValue(PREFIX_NAME);
        Optional<String> subjectValue = arguments.getValue(PREFIX_SUBJECT);
        Optional<String> levelValue = arguments.getValue(PREFIX_LEVEL);
        if (!arguments.getPreamble().isEmpty()
                || nameValue.isEmpty() && subjectValue.isEmpty() && levelValue.isEmpty()) {
            throw new ParseException(FilterCommand.MESSAGE_INVALID_FORMAT);
        }
        if (nameValue.isPresent() && nameValue.get().isBlank()) {
            throw new ParseException(FilterCommand.MESSAGE_EMPTY_NAME);
        }
        List<String> keywords = nameValue.map(value -> List.of(value.split("\\s+"))).orElse(List.of());
        // Regex whitespace does not include every Unicode character that String.isBlank() recognizes.
        if (!keywords.stream().allMatch(ContactMatchesCriteriaPredicate::isValidNameKeyword)) {
            throw new ParseException(FilterCommand.MESSAGE_INVALID_NAME_KEYWORD);
        }
        Optional<Subject> subject = subjectValue.isPresent()
                ? Optional.of(ParserUtil.parseSubject(subjectValue.get())) : Optional.empty();
        Optional<Level> level = levelValue.isPresent()
                ? Optional.of(ParserUtil.parseLevel(levelValue.get())) : Optional.empty();
        return new FilterCommand(new ContactMatchesCriteriaPredicate(keywords, subject, level));
    }
}
