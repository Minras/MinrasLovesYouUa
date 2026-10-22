# Quote selection

`MainActivity` handles button clicks and displays the selected text. `QuoteSelector`
chooses applicable quote categories, combines their non-empty quotes, and selects
one at random. `AndroidQuoteCatalog` maps each quote kind and category to a
statically referenced Android string-array resource.

## Categories

- General quotes are always eligible.
- Personal quotes are eligible when the **Personal quotes** setting is enabled.
  The personal birthday collection is eligible only on the saved birthday, when
  one is set.
- Morning quotes apply from 04:00 through 11:59; day quotes apply from 12:00
  through 17:59; night quotes apply from 23:00 through 03:59. No time-specific
  category applies from 18:00 through 22:59.
- Seasonal quotes follow the calendar seasons: winter (December–February),
  spring (March–May), summer (June–August), and autumn (September–November).
- Halloween quotes apply on October 31 and November 1. Christmas quotes apply
  December 20–27. New Year quotes apply January 1–7 and December 25–31, so the
  Christmas and New Year ranges overlap on December 25–27. Valentine's Day
  quotes apply February 13–15.
- Birthday quotes require both an enabled **Personal quotes** setting and a
  saved month and day that match the local date. The saved birthday repeats
  each year without retaining a year of birth. A February 29 birthday matches
  only on February 29 in leap years.

`MainActivity` captures the local date and time once per quote request. The
selector snapshots the month, day, and hour from that same value before
selecting time, season, holiday, and birthday categories.

The holiday categories are added three times to preserve their existing higher
selection weight. Empty or missing optional collections add no candidates. If
all applicable collections are empty, the selector returns an empty string.

On the first launch after upgrading, the app migrates a previously saved
recognized account to the personal-quotes preference and removes the stored
email. A birthday is stored locally as separate month and day preferences and
can be cleared from Settings.

## Tests

Local JVM unit tests cover hourly and seasonal categories, holiday ranges and
overlaps, personal and birthday quote eligibility, birthday persistence and
clearing, settings migration, and empty or invalid quote collections.
Quote-selection rules are tested without an Android device.
