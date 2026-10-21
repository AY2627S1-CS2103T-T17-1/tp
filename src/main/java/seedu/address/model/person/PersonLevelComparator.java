package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;

/**
 * Orders students by academic level and name, followed by parents ordered by name.
 */
public class PersonLevelComparator implements Comparator<Person> {
    /**
     * Compares contacts by role, academic progression, and case-insensitive name.
     * Legacy students without a level follow students with recorded levels.
     *
     * @param first First contact to compare.
     * @param second Second contact to compare.
     * @return A negative, zero, or positive value according to the display order.
     * @throws NullPointerException If either contact is null.
     */
    @Override
    public int compare(Person first, Person second) {
        requireAllNonNull(first, second);
        if (first.getRole() != second.getRole()) {
            return first.getRole() == Role.STUDENT ? -1 : 1;
        }
        if (first.getRole() == Role.STUDENT) {
            int firstRank = first.getLevel().map(Level::getSortRank).orElse(Integer.MAX_VALUE);
            int secondRank = second.getLevel().map(Level::getSortRank).orElse(Integer.MAX_VALUE);
            int levelComparison = Integer.compare(firstRank, secondRank);
            if (levelComparison != 0) {
                return levelComparison;
            }
        }
        return String.CASE_INSENSITIVE_ORDER.compare(first.getName().fullName, second.getName().fullName);
    }
}
