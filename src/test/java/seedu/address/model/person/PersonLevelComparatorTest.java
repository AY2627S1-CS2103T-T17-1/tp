package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Tests academic progression, name ordering, and legacy contacts in the displayed list.
 */
public class PersonLevelComparatorTest {
    private final PersonLevelComparator comparator = new PersonLevelComparator();

    /**
     * Checks academic progression, name ties, missing legacy levels, and parent ordering together.
     */
    @Test
    public void compare_mixedContacts_ordersStudentsThenParents() {
        Person primary = new PersonBuilder().withName("Zoe").withLevel("Primary 6").build();
        Person secondaryAmy = new PersonBuilder().withName("amy").withLevel("Secondary 1").build();
        Person secondaryBen = new PersonBuilder().withName("Ben").withLevel("Secondary 1").build();
        Person juniorCollege = new PersonBuilder().withName("Alex").withLevel("JC 1").build();
        Person legacy = new PersonBuilder().withName("Aaron").withoutLevel().build();
        Person parentAmy = new PersonBuilder().withRole("parent").withName("Amy").build();
        Person parentBen = new PersonBuilder().withRole("parent").withName("Ben").build();
        List<Person> actual = new ArrayList<>(List.of(parentBen, juniorCollege, secondaryBen,
                legacy, parentAmy, primary, secondaryAmy));

        actual.sort(comparator);

        assertEquals(List.of(primary, secondaryAmy, secondaryBen, juniorCollege, legacy, parentAmy, parentBen), actual);
        for (int index = 1; index < actual.size(); index++) {
            assertTrue(comparator.compare(actual.get(index - 1), actual.get(index)) < 0);
            assertTrue(comparator.compare(actual.get(index), actual.get(index - 1)) > 0);
        }
    }

    /**
     * Checks that case-insensitive names at the same level produce equal sort keys.
     */
    @Test
    public void compare_equalKeys_returnsZero() {
        Person first = new PersonBuilder().withName("Amy").withLevel("Secondary 3").build();
        Person second = new PersonBuilder().withName("amy").withLevel("secondary 3").build();
        assertEquals(0, comparator.compare(first, second));
        assertEquals(0, comparator.compare(first, first));
    }
}
