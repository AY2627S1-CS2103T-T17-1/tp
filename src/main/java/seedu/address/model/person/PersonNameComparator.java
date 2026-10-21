package seedu.address.model.person;

import java.util.Comparator;

/**
 * Orders contacts by their complete names, ignoring case.
 * Equal names compare as zero so the displayed list can retain their insertion order.
 */
public class PersonNameComparator implements Comparator<Person> {
    /**
     * Compares complete contact names without considering case.
     *
     * @param first First contact to compare.
     * @param second Second contact to compare.
     * @return A negative number, zero, or a positive number if the first name precedes, equals, or follows the second.
     * @throws NullPointerException If either contact is {@code null}.
     */
    @Override
    public int compare(Person first, Person second) {
        return String.CASE_INSENSITIVE_ORDER.compare(first.getName().fullName, second.getName().fullName);
    }
}
