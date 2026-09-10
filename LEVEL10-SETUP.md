# Kairo Level-10: JavaFX GUI

This update builds on your Level-9 commit `12af22a` and is intended for `branch-Level-10`.

## Install in your existing project

Save `kairo-level10-update.zip` in Downloads. In IntelliJ's terminal, at the root of your existing `ip` project, switch to the branch you created:

```bash
git switch branch-Level-10
```

Then extract the update directly into the project:

```bash
unzip -o ~/Downloads/kairo-level10-update.zip
```

The archive replaces its named files individually. It includes updated source, tests, the stylesheet, build.gradle, and this guide. Your Git history and saved task file are not part of the archive.

Reload the Gradle project in IntelliJ after updating build.gradle.

## Build and run

Use the course's prescribed Mac JDK:

```bash
sdk use java 25.0.3.fx-zulu
java -version
./gradlew clean test shadowJar
```

Continue when the build reports `BUILD SUCCESSFUL`. The suite contains 15 JUnit tests, including your original tests and the new command-response tests.

Open the GUI:

```bash
./gradlew run
```

You can also run `Launcher.main` in IntelliJ. The existing `Kairo.main` remains available for console use.

Check the built JAR's launcher too:

```bash
java -jar build/libs/kairo.jar
```

## Try the GUI

Enter these commands one at a time. Try both Enter and the Send button.

```text
todo test GUI
list
find GUI
deadline test deadline /by 2026-09-15
event test event /from 2026-09-16 /to 2026-09-17
list
```

Use the task numbers shown by `list` to try `mark N`, `unmark N`, and `delete N` on your test tasks. Use the full `list` to choose task numbers for editing.

Also check:

- `todo` produces an error and you can still enter another command.
- `mark 99999999999999999999` produces an error.
- Long messages wrap, and the conversation scrolls to the newest response.
- Resizing keeps the input box and Send button visible.
- `bye` displays a farewell, then closes the window after one second.
- Reopen the application and run `list`: your tasks should still be present.
- The saved file is still `data/kairo.txt`, relative to the working directory.

## What changed

| File | Purpose |
| --- | --- |
| `src/main/java/kairo/Kairo.java` | Holds the session's tasks and returns command responses. |
| `src/main/java/kairo/Ui.java` | Formats response strings for both interfaces. |
| `src/main/java/kairo/Main.java` | Builds the window and handles Enter, Send, scrolling, and exit. |
| `src/main/java/kairo/Launcher.java` | Starts JavaFX and is the Gradle/JAR entry point. |
| `src/main/java/kairo/DialogBox.java` | Displays a wrapped message with its sender. |
| `src/main/resources/styles/kairo.css` | Styles the chat window. |
| `src/main/java/kairo/Parser.java` | Handles oversized task numbers as command errors. |
| `build.gradle` | Adds JavaFX dependencies and the launcher entry point. |
| `src/test/java/kairo/KairoTest.java` | Tests commands, storage, errors, and exit with temporary files. |
| `src/test/java/kairo/ParserTest.java` | Tests oversized task numbers. |

Kairo's response method has no JavaFX dependency. The GUI and console share command logic, which can be tested without opening a window. The GUI uses JavaFX controls directly; FXML can be introduced as a later refactor.

The dependency list follows the course tutorial, including all Windows, macOS, and Linux classifiers at JavaFX 17.0.7. Java remains version 25.

References:

- [Course requirements](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week4/project.html)
- [JavaFX setup and launcher](https://se-education.org/guides/tutorials/javaFxPart1.html)
- [Controls and layout](https://se-education.org/guides/tutorials/javaFxPart2.html)
- [Event handling and responses](https://se-education.org/guides/tutorials/javaFxPart3.html)

## Verification performed while preparing this update

- All production Java sources compiled with Zulu JDK 25.0.3 and its bundled JavaFX.
- A separate command/storage verification program passed 37 checks, covering every command type, saved formats, reload, invalid input, I/O errors, and exit.
- The Gradle/JUnit build could not finish because network access for that run was blocked in the preparation environment.
- A desktop GUI session was unavailable. Perform the build and visual checks above on your Mac before completing the increment.

## Commit, merge, and tag after verification

Review and commit on the feature branch:

```bash
git status
git add build.gradle src LEVEL10-SETUP.md
git commit -m "Add a JavaFX chat interface while preserving task commands and storage"
```

Use **IntelliJ → Git → Push** to publish `branch-Level-10`.

Merge into master:

```bash
git switch master
git merge --no-ff branch-Level-10 -m "Merge Level-10 GUI for interactive task management"
```

After the merge succeeds, add the lightweight milestone tag:

```bash
git tag Level-10
```

Use **IntelliJ → Git → Push**, select **Push tags → All**, and push. All includes the lightweight tag used for this course.

See [IntelliJ's push documentation](https://www.jetbrains.com/help/idea/commit-and-push-changes.html) for the Push tags option.

Finally:

```bash
git status
git ls-remote --tags origin
```

Check that the output includes `refs/tags/Level-10`.

