package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a contact satisfies every supplied name keyword, subject, and academic level.
 * An empty keyword list or absent field criterion imposes no restriction on that field.
 */
public class ContactMatchesCriteriaPredicate implements Predicate<Person> {
    private final List<String> nameKeywords;
    private final Optional<Subject> subject;
    private final Optional<Level> level;

    /**
     * Creates an immutable filter with at least one criterion.
     *
     * @param nameKeywords Complete name words to match, ignoring case.
     * @param subject Complete subject to match, ignoring case, or empty to omit this criterion.
     * @param level Complete academic level to match, ignoring case, or empty to omit this criterion.
     * @throws NullPointerException If an argument or a name keyword is {@code null}.
     * @throws IllegalArgumentException If no criterion is supplied or a name keyword is blank or contains whitespace.
     */
    public ContactMatchesCriteriaPredicate(List<String> nameKeywords, Optional<Subject> subject,
            Optional<Level> level) {
        this.nameKeywords = List.copyOf(nameKeywords);
        this.subject = requireNonNull(subject);
        this.level = requireNonNull(level);
        checkArgument(!nameKeywords.isEmpty() || subject.isPresent() || level.isPresent(),
                "At least one filter criterion is required.");
        checkArgument(nameKeywords.stream().allMatch(ContactMatchesCriteriaPredicate::isValidNameKeyword),
                "Name keywords must be nonblank single words.");
    }

    /**
     * Returns whether a keyword is a nonblank single word under the filter's whitespace rules.
     *
     * @param keyword Keyword to validate.
     * @return {@code true} if the keyword is nonblank and contains no regex whitespace.
     * @throws NullPointerException If {@code keyword} is {@code null}.
     */
    public static boolean isValidNameKeyword(String keyword) {
        requireNonNull(keyword);
        return !keyword.isBlank() && keyword.matches("\\S+");
    }

    /**
     * Checks all supplied criteria using case-insensitive whole-word name and whole-value field comparisons.
     * A missing contact field cannot satisfy a criterion for that field.
     *
     * @param person Contact to check.
     * @return {@code true} if the contact satisfies every supplied criterion.
     * @throws NullPointerException If {@code person} is {@code null}.
     */
    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return nameKeywords.stream()
                // Names are stored in NFC form, so a keyword typed with combining accents still matches.
                .allMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName,
                        Name.normalize(keyword)))
                && subject.map(expected -> person.getSubject()
                        .map(actual -> actual.value.equalsIgnoreCase(expected.value)).orElse(false)).orElse(true)
                && level.map(expected -> person.getLevel()
                        .map(actual -> actual.value.equalsIgnoreCase(expected.value)).orElse(false)).orElse(true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContactMatchesCriteriaPredicate otherPredicate)) {
            return false;
        }
        return nameKeywords.equals(otherPredicate.nameKeywords)
                && subject.equals(otherPredicate.subject)
                && level.equals(otherPredicate.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameKeywords, subject, level);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("subject", subject)
                .add("level", level)
                .toString();
    }
}
