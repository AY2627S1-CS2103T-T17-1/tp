---
layout: page
title: User Guide
---

TutorContacts is a **desktop contact manager for independent private tutors, optimized for use through a Command Line
Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). It keeps student and parent contact
details, together with each student's subject and academic level, in one place. If you type quickly, TutorContacts can
help you find and manage this information faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `tutorcontacts.jar` file from [here](https://github.com/AY2627S1-CS2103T-T17-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for TutorContacts.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar tutorcontacts.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. On the first launch, the contact list is empty.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add r/student n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a student contact named `John Doe`.

   * `view 1` : Shows the 1st contact in the current list.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Command words are case-insensitive. For example, `list`, `List`, and `LIST` are equivalent.<br>
  Parameter values retain their capitalization.

* Extraneous parameters for `help`, `exit`, and `clear` are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* The `list` command accepts no parameters. Additional arguments are rejected.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student or parent contact: `add`

Adds a student or parent contact to TutorContacts.

Format: `add r/ROLE n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

* `ROLE` must be `student` or `parent` and is case-insensitive.
* The `r/ROLE` parameter is required and may appear in any parameter order.
* On success, TutorContacts reports `Added student: NAME.` or `Added parent: NAME.`
* The newly added contact is selected and scrolled into view as a compact card.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero.
</div>

Examples:
* `add r/student n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add r/PARENT n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all contacts: `list`

Displays all contacts in insertion order, numbered from 1. If the list was filtered by a search, this command restores the complete list.

Compact cards show only the index, role, name, and phone. `list` collapses any expanded card.

Format: `list`

* If contacts exist, the result message is `Showing N contacts.`, where `N` is the total number of contacts.
* If no contacts exist, the result message is `No contacts to display.`
* Command words are case-insensitive: `list`, `List`, and `LIST` are accepted.
* Leading and trailing spaces are ignored.
* Additional arguments are rejected with `Invalid command format. Expected: list`.
* This command does not change contact details.

Example: `list`

### Viewing a contact: `view`

Shows the details of the specified contact from TutorContacts.

Format: `view INDEX`

* Views the contact at the specified `INDEX` in the currently displayed list, including search results.
* Supply exactly one **positive integer**: `1`, `2`, `3`, … Use digits only; leading zeros are accepted.
* On success, the message is `Showing contact: NAME.`
* This command does not change contact details or save the data file.
* The selected card is highlighted and expanded in place; other cards remain compact.
* Expanded cards also show email, address, and note, plus subject and academic level for students.
  An absent note is shown as `None`. Older student records without subject or level show `Not recorded` for those fields.
  Existing tags, if any, appear only in the expanded card.
* Use `view` on another contact to move the expansion, or `list` to collapse it.

Examples:
* `list` followed by `view 2` shows the second displayed contact.
* `find Betsy` followed by `view 1` shows the first search result.

Invalid input leaves contact data, the displayed list, and the current selection and expansion unchanged:

| Input problem | Error message |
| --- | --- |
| Missing or extra arguments | `Invalid command format. Expected: view INDEX` |
| An index that is not a positive integer | `Index must be a positive integer shown in the current list.` |
| An index outside the displayed list | `No contact exists at index INDEX in the current list.` |

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a contact: `delete`

Deletes the specified contact from TutorContacts.

Format: `delete INDEX`

* Deletes the contact at the specified `INDEX` in the currently displayed list, including search results.
* Supply exactly one **positive integer**: `1`, `2`, `3`, … Use digits only; leading zeros are accepted.
* On success, the message is `Deleted contact: NAME.` The remaining contacts keep their order and are renumbered.
* Deleting the selected contact clears the selection and expansion. Deleting another contact keeps the selected
  contact highlighted, with its new index and any expanded details.

Examples:
* `list` followed by `delete 2` deletes the second displayed contact.
* `find Betsy` followed by `delete 1` deletes the first search result.

Invalid input leaves contact data and the displayed list unchanged:

| Input problem | Error message |
| --- | --- |
| Missing or extra arguments | `Invalid command format. Expected: delete INDEX` |
| An index that is not a positive integer | `Index must be a positive integer shown in the current list.` |
| An index outside the displayed list | `No contact exists at index INDEX in the current list.` |

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits TutorContacts. The feedback message is `Exiting TutorContacts.`

Successful contact changes are saved when you make them, so exiting does not rewrite the data file.
If a change cannot be saved, TutorContacts rejects it and restores the previous contact list; exiting does not retry it.

Format: `exit`

### Saving the data

TutorContacts automatically saves contact data after commands that change it, such as `add`, `edit`, `delete`, and
`clear`. You do not need to save manually. Read-only commands such as `list`, `find`, `help`, and `exit` do not rewrite
the data file. A failed save leaves both the displayed contacts and the existing data file unchanged.

### Editing the data file

Contact data is saved as a JSON file at `[JAR file location]/data/tutorcontacts.json`. The previous default file,
`data/addressbook.json`, is not loaded automatically; copy it to the new path before starting TutorContacts if you want
to keep those contacts. If the data file is missing, TutorContacts starts with an empty list and creates the file after
the first successful data-changing command. Advanced users can edit the JSON file directly.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If the data file is invalid or unreadable, TutorContacts starts with an empty list and displays
`Warning: TutorContacts could not load data/tutorcontacts.json. The existing file was not changed.`
TutorContacts will not overwrite that file during the session; repair it and restart the app before changing contacts.
Back up the file before editing it.<br>
Certain edits can also cause TutorContacts to behave unexpectedly (for example, if a value is outside the acceptable
range). Edit the data file only if you are confident that you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous TutorContacts home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add r/ROLE n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add r/student n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List** | `list`
**View** | `view INDEX`<br> e.g., `view 2`
**Help** | `help`
