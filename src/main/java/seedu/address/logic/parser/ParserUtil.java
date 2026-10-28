package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Subject;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code indexValue} as a positive index in the displayed contact list.
     * Only digits are accepted, so signs, decimals, and spaces are rejected, while leading zeros are allowed.
     * The value is parsed without integer overflow, so an index too large for an {@code int} is reported as absent
     * from the list rather than as malformed.
     *
     * @param indexValue Decimal index supplied by the user, without surrounding whitespace.
     * @return The corresponding one-based index.
     * @throws ParseException If the value is not a positive integer, or exceeds the largest supported index.
     */
    public static Index parseDisplayedIndex(String indexValue) throws ParseException {
        requireNonNull(indexValue);
        if (!indexValue.matches("[0-9]+")) {
            throw new ParseException(Messages.MESSAGE_INVALID_DISPLAYED_INDEX);
        }

        BigInteger numericIndex = new BigInteger(indexValue);
        if (numericIndex.signum() == 0) {
            throw new ParseException(Messages.MESSAGE_INVALID_DISPLAYED_INDEX);
        }

        // An index too large for an int cannot exist in the displayed list.
        if (numericIndex.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new ParseException(String.format(Messages.MESSAGE_DISPLAYED_INDEX_OUT_OF_RANGE, numericIndex));
        }

        return Index.fromOneBased(numericIndex.intValueExact());
    }

    /**
     * Parses {@code role} into a {@code Role}.
     * Leading and trailing whitespaces are trimmed.
     *
     * @throws ParseException If the specified role is not supported.
     */
    public static Role parseRole(String role) throws ParseException {
        requireNonNull(role);
        String trimmedRole = role.trim();
        if (!Role.isValidRole(trimmedRole)) {
            throw new ParseException(Role.MESSAGE_CONSTRAINTS);
        }
        return Role.fromString(trimmedRole);
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        if (!Address.isValidAddress(address)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(address);
    }

    /**
     * Parses and normalizes a student's subject.
     *
     * @param subject Subject supplied by the user.
     * @return The validated subject.
     * @throws ParseException If the subject violates its field constraints.
     */
    public static Subject parseSubject(String subject) throws ParseException {
        requireNonNull(subject);
        if (!Subject.isValidSubject(subject)) {
            throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
        }
        return new Subject(subject);
    }

    /**
     * Parses and normalizes a student's academic level.
     *
     * @param level Academic level supplied by the user.
     * @return The validated academic level.
     * @throws ParseException If the level violates its field constraints.
     */
    public static Level parseLevel(String level) throws ParseException {
        requireNonNull(level);
        if (!Level.isValidLevel(level)) {
            throw new ParseException(Level.MESSAGE_CONSTRAINTS);
        }
        return new Level(level);
    }

    /**
     * Parses an optional note, treating an empty or whitespace-only value as omitted.
     *
     * @param note Note supplied by the user, or an empty string if omitted.
     * @return The validated note, if present.
     * @throws ParseException If a nonempty note violates its field constraints.
     */
    public static Optional<Note> parseNote(String note) throws ParseException {
        requireNonNull(note);
        if (note.trim().isEmpty()) {
            return Optional.empty();
        }
        if (!Note.isValidNote(note)) {
            throw new ParseException(Note.MESSAGE_CONSTRAINTS);
        }
        return Optional.of(new Note(note));
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
