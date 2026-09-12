# Sorting tasks

Use these commands in Kairo's chat box or console:

| Command | Result |
| --- | --- |
| `sort name` | Sort all tasks by description in ascending alphabetical order, ignoring capitalisation. |
| `sort date` | Sort dated tasks from earliest to latest, using deadline dates and event start dates. Todos appear last. |

Both commands display the complete list with its new numbering and save that
order. The order is restored when Kairo starts again. Completed and incomplete
tasks are both included.

Tasks with equal names, ignoring case, keep their relative order when sorting by
name. Tasks with equal dates keep their relative order when sorting by date, as
do the todos at the end of that list. Sorting never changes task descriptions,
dates, or completion states.

After sorting, use the newly displayed task numbers with `mark`, `unmark`, and
`delete`. Newly added tasks are appended to the list; run a sort command again
when you want to reorder them.

## Examples

For these three tasks:

1. `todo Zebra`
2. `deadline apple /by 2026-10-01`
3. `event Meeting /from 2026-09-20 /to 2026-10-10`

`sort name` displays **apple, Meeting, Zebra**.

`sort date` displays **Meeting, apple, Zebra**. The meeting is placed using its
start date, even though it ends after the deadline.

## Invalid input

The command and field must be lowercase. Missing, unsupported, or extra fields
produce this error:

```text
OOPS! Use: sort name OR sort date
```

Examples include `sort`, `sort priority`, `sort NAME`, and `sort date extra`.
These inputs do not change the task list.

Sorting an empty list displays `Your task list is empty.`
If saving fails, Kairo reports the save error and keeps the active task order.

## Testing

Run the test suite with Java 25:

```bash
./gradlew test
```

`SortTest` covers command validation, mixed task types, equal names and dates,
empty and single-task lists, todos after the latest possible date, saved order
after restarting, task numbers after sorting, and save failures.
