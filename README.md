# Kairo

Kairo is a Java 25 desktop task manager with a JavaFX chat interface. It supports
todos, deadlines, events, completion tracking, search, sorting, and automatic saving.

[User Guide](https://tiannnle.github.io/ip/) ·
[Download a release](https://github.com/tiannnle/ip/releases)

## Run and develop

Open this project in IntelliJ and use **JDK 25** for the project and Gradle.
Run these commands from the project folder:

```bash
./gradlew test
./gradlew run
./gradlew shadowJar
```

On Windows, use `gradlew.bat` in place of `./gradlew`.
The packaged application is `build/libs/kairo.jar`. Run it with:

```bash
java -jar build/libs/kairo.jar
```

The GUI launcher is `kairo.Launcher`. The application uses `data/kairo.txt` relative
to the working directory. The website content is maintained in `docs/README.md`.

## Project origins

Kairo was developed for the NUS CS2103T individual project, starting from the
[course iP template](https://github.com/NUS-CS2103-AY2627-S1/ip).
