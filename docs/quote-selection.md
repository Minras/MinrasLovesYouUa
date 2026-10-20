# Quote selection

`MainActivity` handles button clicks and displays the selected text. `QuoteSelector`
chooses applicable quote categories, combines their non-empty quotes, and selects
one at random. `AndroidQuoteCatalog` maps each quote kind and category to a
statically referenced Android string-array resource.

## Categories

- General quotes are always eligible.
- Personal quotes are eligible for accounts in the existing personal-account
  allowlist. The personal birthday collection is eligible on February 8.
- Morning quotes apply from 05:00 through 11:59; day quotes apply from 12:00
  through 17:59; night quotes apply from 23:00 through 03:59.
- Seasonal quotes follow the calendar seasons: winter (December–February),
  spring (March–May), summer (June–August), and autumn (September–November).
- Holiday quotes apply to the configured Halloween, Christmas, New Year, and
  Valentine's Day date ranges in `QuoteSelector`.

The holiday categories are added three times to preserve their existing higher
selection weight. Empty or missing optional collections add no candidates. If
all applicable collections are empty, the selector returns an empty string.
