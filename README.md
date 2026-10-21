    # MinrasLovesYouUa

An Android app built with Gradle.

## Requirements

- JDK 17 (Oracle JDK, as selected by the Gradle daemon toolchain)
- Android SDK Platform 35

Open the project in Android Studio and install Android SDK Platform 35 from SDK Manager. Android Studio creates the machine-specific `local.properties` file; do not commit that file.

## Build and verify

Run these commands from the repository root:

```sh
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
./gradlew bundleRelease
```

To run the same checks as GitHub Actions in one invocation:

```sh
./gradlew --stacktrace testDebugUnitTest lintDebug assembleDebug bundleRelease
```

GitHub Actions runs these checks for pull requests and pushes to `master`. It uploads the debug APK and available test and lint reports as workflow artifacts. The release bundle is built unsigned; CI does not sign or publish app packages.
