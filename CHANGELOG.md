# Changelog

## Next Release

- Add a personal-quotes setting, migrate recognized saved accounts to it, and remove saved account emails and Google account selection.
- Refresh the main screen from persisted settings after returning from Settings.
- Make quote time and date category selection use one local snapshot, with explicit hour and calendar date rules, including morning quotes from 04:00.
- Replace the arithmetic sample test with behavior-focused quote and settings coverage, including overlapping holiday dates and safe selection from empty or invalid collections.
- Add GitHub Actions checks for Android builds and automated Gradle and Actions dependency updates.
- Remove the unused Apache Commons Lang dependency.
- Keep menu, settings, and accessibility copy in Ukrainian string resources, with default resources serving as the Ukrainian locale fallback.
- Add an optional locally stored birthday setting and show birthday quotes only when personal quotes are enabled and the saved month and day match.
