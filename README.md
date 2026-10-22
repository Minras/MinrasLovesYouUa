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

## Personal quotes setting

Personal quotes can be enabled or disabled in Settings without selecting a Google account. On first launch after upgrading, a previously saved recognized account enables personal quotes; the saved email is then removed. Other saved accounts leave personal quotes disabled.

An optional birthday can also be set or cleared in Settings. The app stores only the birthday's month and day on the device. Birthday quotes are eligible on that date when personal quotes are enabled; a February 29 birthday matches on leap days only.

To run the same checks as GitHub Actions in one invocation:

```sh
./gradlew --stacktrace testDebugUnitTest lintDebug assembleDebug bundleRelease
```

## UI language

Visible interface and accessibility text is kept in `app/src/main/res/values/strings.xml` in Ukrainian. These default resources also provide the fallback for Ukrainian locale devices, so the copy stays in one place until another locale is translated.

GitHub Actions runs these checks for pull requests and pushes to `master`. It uploads the debug APK and available test and lint reports as workflow artifacts. The release bundle is built unsigned; CI does not sign or publish app packages.
