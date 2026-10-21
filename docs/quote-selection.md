# Quote selection

`MainActivity` handles button clicks and displays the selected text. `QuoteSelector`
chooses applicable quote categories, combines their non-empty quotes, and selects
one at random. `AndroidQuoteCatalog` maps each quote kind and category to a
statically referenced Android string-array resource.

## Categories

- General quotes are always eligible.
- Personal quotes are eligible for accounts in the existing personal-account
  allowlist. The personal birthday collection is eligible on February 8.
- Morning quotes apply from 04:00 through 11:59; day quotes apply from 12:00
  through 17:59; night quotes apply from 23:00 through 03:59. No time-specific
  category applies from 18:00 through 22:59.
- Seasonal quotes follow the calendar seasons: winter (December–February),
  spring (March–May), summer (June–August), and autumn (September–November).
- Halloween quotes apply on October 31 and November 1. Christmas quotes apply
  December 20–27. New Year quotes apply January 1–7 and December 25–31, so the
  Christmas and New Year ranges overlap on December 25–27. Valentine's Day
  quotes apply February 13–15.
- The personal birthday collection applies on February 8 for eligible personal
  accounts.

`MainActivity` captures the local date and time once per quote request. The
selector snapshots the month, day, and hour from that same value before
selecting time, season, holiday, and birthday categories.

The holiday categories are added three times to preserve their existing higher
selection weight. Empty or missing optional collections add no candidates. If
all applicable collections are empty, the selector returns an empty string.
