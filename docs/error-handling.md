# Error handling in Kairo

This update implements `A-MoreErrorHandling` and retains `sort name` and `sort date`.

## Command rules

- Enter one command at a time. Blank input produces a helpful error.
- Command words and sort fields remain lowercase.
- `list` and `bye` take no arguments. For example, `bye now` returns an error and keeps Kairo open.
- Spaces or tabs may separate date markers and values. Spaces inside descriptions are preserved.
- A deadline needs exactly one `/by` marker. An event needs exactly one `/from` followed by one `/to`.
- Standalone `/by`, `/from`, and `/to` are reserved markers inside deadline and event commands.
- Dates remain in `yyyy-MM-dd` format. Invalid calendar dates are rejected.
- An event may start and end on the same day; its end cannot precede its start.
- New task descriptions cannot contain the pipe character (`|`) or line breaks.
- A rejected command does not change tasks or write a data file.

## Saving and loading

Successful task changes are saved to `data/kairo.txt` as before. The text format is unchanged.
A missing file or folder is created on the first successful task change.

If saving fails, Kairo reports the failure and retains the previous task contents,
completion status, and order. Correct the storage problem and retry the command.

If saved data is invalid, the welcome message identifies the line that needs attention.
Kairo blocks task commands for that session to protect the existing file. Close Kairo,
make a backup before editing the file, correct the reported problem, then restart.
Kairo does not delete malformed records or silently start overwriting the damaged file.

Saving first writes a temporary file in the data folder, then atomically replaces the task file.
If the location does not support atomic replacement, saving reports an error; use a local
folder that supports it. A cleanup failure can leave an unused `kairo-*.tmp` file, which is
never read as task data.

## Quick manual check

Run `./gradlew test`, then `./gradlew run` using Java 25. Enter these commands in Kairo:

| Command | Expected result |
| --- | --- |
| `deadline report   /by   2026-09-18` | Adds the deadline. |
| `event meeting /from 2026-09-19 /to 2026-09-18` | Reports that the end date cannot precede the start. |
| `event meeting /from 2026-09-18 /to 2026-09-18` | Adds a same-day event. |
| `deadline report /by 2026-09-18 /by 2026-09-19` | Shows the deadline command format. |
| `todo read \| write` | Asks you to remove the pipe character. |
| `bye now` | Shows the bye command format and keeps the app open. |
| `sort date` | Still sorts tasks by date. |
| `list` | Shows only successfully added tasks. |

`ErrorHandlingTest` uses temporary folders to check malformed commands, invalid saved records,
blocked overwrites, save failures, and recovery. Existing task data is not used for these tests.
