# Kairo GUI checks

This update implements `A-BetterGui` through distinct message layouts, visible errors,
responsive sizing, and easier command discovery.

## What changed

- Your commands appear in compact blue bubbles aligned to the right.
- Kairo replies use wider white cards for task lists and longer responses.
- Errors have a pale red card and the explicit heading **Kairo · Error**.
- After an error, the command stays in the input box for correction. Editing it clears
  the red input border. Successful commands clear the input as before.
- The **Commands** menu fills the input with an editable example. Select **Send** or
  press **Enter** to execute it. The menu includes both sorting commands.
- **Send** is disabled when the input contains only whitespace.
- Resizing wraps messages to the available width. New replies scroll into view;
  resizing alone no longer forces the conversation to the bottom.

## Verify on your computer

Use Java 25. From the project folder, run `./gradlew test`, then `./gradlew run`.

| Action | Expected result |
| --- | --- |
| Open Kairo. | The title says Kairo and the Commands menu is visible. |
| Select Commands, then Add a todo. | The input shows `todo read book`; the task is not added yet. |
| Press Enter. | The blue command bubble and a wider Kairo reply appear; the input clears. |
| Type `event meeting /from 2026-09-19 /to 2026-09-18` and press Enter. | A red error card appears and the command stays available for editing. |
| Correct the end date to `2026-09-19`, then press Enter. | The input border clears while editing; the corrected event is added. |
| Select Commands, then Sort by date, then Send. | Kairo displays the sorted list. |
| Leave the input blank or type only spaces. | Send is disabled. |
| Add a task with a long description and use `list`. | The text wraps and stays readable. |
| Make the window narrow, then widen it again. | Message widths adjust; input controls remain visible. |
| Scroll to older messages, then resize the window. | Resizing does not trigger an explicit jump to the latest reply. |
| Enter `bye now`. | The error remains visible and Kairo stays open. |
| Correct the input to `bye` and press Enter. | Kairo shows its farewell and closes after about one second. |

These checks use the normal application. Any tasks you add will be saved to your task list.

## Files in this update

- `src/main/java/kairo/Main.java`
- `src/main/java/kairo/DialogBox.java`
- `src/main/resources/styles/kairo.css`
- `docs/gui-checks.md`
