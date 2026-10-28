package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Tests AND matching, exact field comparisons, missing values, and immutable filter criteria.
 */
public class ContactMatchesCriteriaPredicateTest {
    private final Person alice = new PersonBuilder().withName("Alice Tan")
            .withSubject("Additional Mathematics").withLevel("Secondary 3").build();

    /**
     * Checks every criterion combination and rejects a mismatch in any supplied field.
     */
    @Test
    public void test_everyNonemptyCombination_requiresAllSuppliedCriteria() {
        for (int combination = 1; combination < 8; combination++) {
            List<String> keywords = (combination & 1) == 0 ? List.of() : List.of("tan", "ALICE");
            Optional<Subject> subject = (combination & 2) == 0
                    ? Optional.empty() : Optional.of(new Subject(" additional   MATHEMATICS "));
            Optional<Level> level = (combination & 4) == 0
                    ? Optional.empty() : Optional.of(new Level(" secondary   3 "));
            ContactMatchesCriteriaPredicate predicate = new ContactMatchesCriteriaPredicate(keywords, subject, level);
            assertTrue(predicate.test(alice));
            if (!keywords.isEmpty()) {
                assertFalse(predicate.test(new PersonBuilder(alice).withName("Alice Lim").build()));
            }
            if (subject.isPresent()) {
                assertFalse(predicate.test(new PersonBuilder(alice).withSubject("Mathematics").build()));
            }
            if (level.isPresent()) {
                assertFalse(predicate.test(new PersonBuilder(alice).withLevel("Secondary 4").build()));
            }
        }
    }

    /**
     * Checks case-insensitive whole-word AND matching, including repeated keywords and punctuation.
     */
    @Test
    public void test_nameKeywords_usesWholeWordsAndAllKeywords() {
        assertTrue(names("tan", "ALICE", "tan").test(alice));
        assertFalse(names("Ali").test(alice));
        assertFalse(names("Alice", "Bob").test(alice));
        assertFalse(names("Mathematics").test(alice));
        Person hyphenated = new PersonBuilder(alice).withName("Alice-Marie O'Neil").build();
        assertTrue(names("alice-marie", "o'neil").test(hyphenated));
        assertFalse(names("Alice").test(hyphenated));
    }

    /**
     * Checks that subject fragments and different academic levels cannot match stored values.
     */
    @Test
    public void test_subjectAndLevel_requiresCompleteMatchingValues() {
        assertFalse(criteria("Mathematics", null).test(alice));
        assertFalse(criteria("Additional", null).test(alice));
        assertFalse(criteria(null, "Secondary 4").test(alice));
        assertFalse(criteria("Additional Mathematics", "JC 1").test(alice));
    }

    /**
     * Checks that missing fields only prevent matches when their criteria are supplied.
     */
    @Test
    public void test_missingFields_onlyMatchesUnrestrictedFields() {
        Person parent = new PersonBuilder(alice).withRole("parent").build();
        Person legacy = new PersonBuilder(alice).withoutSubject().withoutLevel().build();
        for (Person person : List.of(parent, legacy)) {
            assertTrue(names("Alice").test(person));
            assertFalse(criteria("Additional Mathematics", null).test(person));
            assertFalse(criteria(null, "Secondary 3").test(person));
        }
        assertTrue(criteria("Additional Mathematics", null).test(new PersonBuilder(alice).withoutLevel().build()));
        assertTrue(criteria(null, "Secondary 3").test(new PersonBuilder(alice).withoutSubject().build()));
    }

    /**
     * Checks that later changes to the caller's keyword list do not change the predicate.
     */
    @Test
    public void constructor_mutableKeywords_copiesInput() {
        List<String> keywords = new ArrayList<>(List.of("Alice"));
        ContactMatchesCriteriaPredicate predicate =
                new ContactMatchesCriteriaPredicate(keywords, Optional.empty(), Optional.empty());
        keywords.add("Bob");
        assertTrue(predicate.test(alice));
    }

    /**
     * Checks rejection of absent criteria, malformed keywords, and null inputs.
     */
    @Test
    public void constructor_invalidCriteria_rejectsInput() {
        assertThrows(IllegalArgumentException.class, () -> names());
        for (String keyword : List.of("", " ", "\u2003", "\u2002", "\u3000", "Alice Bob", "Alice\nBob\nTan")) {
            assertThrows(IllegalArgumentException.class, () -> names(keyword));
        }
        assertThrows(NullPointerException.class, () ->
                new ContactMatchesCriteriaPredicate(null, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new ContactMatchesCriteriaPredicate(List.of("Alice"), null, Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new ContactMatchesCriteriaPredicate(List.of("Alice"), Optional.empty(), null));
        assertThrows(NullPointerException.class, () -> names("Alice").test(null));
    }

    /**
     * Checks that predicate equality includes name keywords, subject, and level.
     */
    @Test
    public void equals_comparesAllCriteria() {
        ContactMatchesCriteriaPredicate predicate = criteria("Mathematics", "Secondary 3");
        assertEquals(predicate, predicate);
        assertEquals(predicate, criteria("Mathematics", "Secondary 3"));
        assertFalse(predicate.equals(criteria("Physics", "Secondary 3")));
        assertFalse(predicate.equals(criteria("Mathematics", "Secondary 4")));
        assertFalse(predicate.equals(names("Alice")));
        assertFalse(names("Alice").equals(names("Bob")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("Mathematics"));
    }

    /**
     * Checks that independently constructed equal predicates can be found and deduplicated in a hash set.
     */
    @Test
    public void hashCode_equalCriteria_supportsHashSetLookup() {
        ContactMatchesCriteriaPredicate predicate = new ContactMatchesCriteriaPredicate(List.of("Alice", "Tan"),
                Optional.of(new Subject("Mathematics")), Optional.of(new Level("Secondary 3")));
        ContactMatchesCriteriaPredicate equivalent = new ContactMatchesCriteriaPredicate(List.of("Alice", "Tan"),
                Optional.of(new Subject("Mathematics")), Optional.of(new Level("Secondary 3")));
        Set<ContactMatchesCriteriaPredicate> predicates = new HashSet<>();
        predicates.add(predicate);

        assertEquals(predicate, equivalent);
        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertTrue(predicates.contains(equivalent));
        assertFalse(predicates.add(equivalent));
        assertEquals(1, predicates.size());
    }

    private ContactMatchesCriteriaPredicate names(String... keywords) {
        return new ContactMatchesCriteriaPredicate(List.of(keywords), Optional.empty(), Optional.empty());
    }

    private ContactMatchesCriteriaPredicate criteria(String subject, String level) {
        return new ContactMatchesCriteriaPredicate(List.of(), Optional.ofNullable(subject).map(Subject::new),
                Optional.ofNullable(level).map(Level::new));
    }
}
