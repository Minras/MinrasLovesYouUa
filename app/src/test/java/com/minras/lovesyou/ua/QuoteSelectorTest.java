package com.minras.lovesyou.ua;

import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class QuoteSelectorTest {

    @Test
    public void selectsFromTheRequestedQuoteKind() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.GENERAL, "ask quote");
        catalog.set(QuoteSelector.QuoteKind.TELL, QuoteSelector.Category.GENERAL, "tell quote");
        QuoteSelector selector = selector(catalog);

        assertEquals("ask quote", selector.select(
                QuoteSelector.QuoteKind.ASK, false, date(Calendar.JUNE, 10, 19)));
        assertEquals("tell quote", selector.select(
                QuoteSelector.QuoteKind.TELL, false, date(Calendar.JUNE, 10, 19)));
    }

    @Test
    public void ignoresEmptyOptionalCategoriesAndStillSelectsGeneralQuote() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.GENERAL, "general quote");
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.WINTER);
        QuoteSelector selector = selector(catalog);

        assertEquals("general quote", selector.select(
                QuoteSelector.QuoteKind.ASK, false, date(Calendar.JANUARY, 10, 19)));
    }

    @Test
    public void returnsEmptyStringWhenEveryApplicableCategoryIsEmpty() {
        QuoteSelector selector = selector(new FakeCatalog());

        assertEquals("", selector.select(
                QuoteSelector.QuoteKind.ASK, false, date(Calendar.JUNE, 10, 19)));
    }

    @Test
    public void includesPersonalQuotesWhenPreferenceIsEnabled() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.TELL, QuoteSelector.Category.PERSONAL, "personal quote");
        QuoteSelector selector = selector(catalog);

        assertEquals("personal quote", selector.select(
                QuoteSelector.QuoteKind.TELL,
                true,
                date(Calendar.JUNE, 10, 19)));
    }

    @Test
    public void excludesPersonalQuotesWhenPreferenceIsDisabled() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.TELL, QuoteSelector.Category.PERSONAL, "personal quote");
        QuoteSelector selector = selector(catalog);

        assertEquals("", selector.select(QuoteSelector.QuoteKind.TELL, false,
                date(Calendar.JUNE, 10, 19)));
    }

    @Test
    public void mapsEveryHourToItsDocumentedCategoryOrNoTimeCategory() {
        FakeCatalog catalog = new FakeCatalog();
        for (QuoteSelector.Category category : new QuoteSelector.Category[] {
                QuoteSelector.Category.MORNING,
                QuoteSelector.Category.DAY,
                QuoteSelector.Category.NIGHT
        }) {
            catalog.set(QuoteSelector.QuoteKind.ASK, category, category.name());
        }
        QuoteSelector selector = selector(catalog);
        String[] expectedCategoriesByHour = {
                "NIGHT", "NIGHT", "NIGHT", "NIGHT",
                "MORNING", "MORNING", "MORNING", "MORNING",
                "MORNING", "MORNING", "MORNING", "MORNING",
                "DAY", "DAY", "DAY", "DAY", "DAY", "DAY",
                "", "", "", "", "", "NIGHT"
        };

        for (int hour = 0; hour < 24; hour++) {
            assertEquals("Unexpected time category at hour " + hour,
                    expectedCategoriesByHour[hour],
                    selector.select(QuoteSelector.QuoteKind.ASK, false,
                            date(Calendar.JUNE, 10, hour)));
        }
    }

    @Test
    public void fourAmUsesMorningCategory() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.MORNING, "morning quote");
        QuoteSelector selector = selector(catalog);

        assertEquals("morning quote", selector.select(QuoteSelector.QuoteKind.ASK, false,
                date(Calendar.JUNE, 10, 4)));
    }

    @Test
    public void keepsTheDocumentedSeasonForEveryMonth() {
        FakeCatalog catalog = new FakeCatalog();
        for (QuoteSelector.Category category : new QuoteSelector.Category[] {
                QuoteSelector.Category.WINTER,
                QuoteSelector.Category.SPRING,
                QuoteSelector.Category.SUMMER,
                QuoteSelector.Category.AUTUMN
        }) {
            catalog.set(QuoteSelector.QuoteKind.ASK, category, category.name());
        }
        QuoteSelector selector = selector(catalog);
        String[] expectedCategoriesByMonth = {
                "WINTER", "WINTER", "SPRING", "SPRING", "SPRING", "SUMMER",
                "SUMMER", "SUMMER", "AUTUMN", "AUTUMN", "AUTUMN", "WINTER"
        };

        for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
            assertEquals("Unexpected season in month " + (month + 1),
                    expectedCategoriesByMonth[month],
                    selector.select(QuoteSelector.QuoteKind.ASK, false,
                            date(month, 10, 4)));
        }
    }

    @Test
    public void includesBothHolidayCollectionsAcrossTheirOverlappingDates() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.XMAS, "Christmas quote");
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.NEWYEAR, "New Year quote");

        for (int day : new int[] {25, 26, 27}) {
            for (int candidateIndex = 0; candidateIndex < 6; candidateIndex++) {
                String expectedQuote = candidateIndex < 3 ? "Christmas quote" : "New Year quote";
                QuoteSelector selector = new QuoteSelector(catalog, new FixedRandom(candidateIndex));

                assertEquals("Unexpected holiday quote on December " + day
                                + " at candidate " + candidateIndex,
                        expectedQuote,
                        selector.select(QuoteSelector.QuoteKind.ASK, false,
                                date(Calendar.DECEMBER, day, 19)));
            }
        }
    }

    @Test
    public void keepsHolidayDateRangesExplicit() {
        assertDateRange(QuoteSelector.Category.HALLOWEEN,
                new int[][] {{Calendar.OCTOBER, 31}, {Calendar.NOVEMBER, 1}},
                new int[][] {{Calendar.OCTOBER, 30}, {Calendar.NOVEMBER, 2}});
        assertDateRange(QuoteSelector.Category.XMAS,
                new int[][] {{Calendar.DECEMBER, 20}, {Calendar.DECEMBER, 27}},
                new int[][] {{Calendar.DECEMBER, 19}, {Calendar.DECEMBER, 28}});
        assertDateRange(QuoteSelector.Category.NEWYEAR,
                new int[][] {{Calendar.JANUARY, 1}, {Calendar.JANUARY, 7},
                        {Calendar.DECEMBER, 25}, {Calendar.DECEMBER, 31}},
                new int[][] {{Calendar.DECEMBER, 24}, {Calendar.JANUARY, 8}});
        assertDateRange(QuoteSelector.Category.VALENTINES,
                new int[][] {{Calendar.FEBRUARY, 13}, {Calendar.FEBRUARY, 15}},
                new int[][] {{Calendar.FEBRUARY, 12}, {Calendar.FEBRUARY, 16}});
    }

    @Test
    public void birthdayQuotesRequireEnabledPersonalQuotesAndMatchingSavedBirthday() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK,
                QuoteSelector.Category.PERSONAL_BIRTHDAY, "birthday quote");
        QuoteSelector selector = selector(catalog);

        assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK,
                true, date(Calendar.FEBRUARY, 8, 4)));
        assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK,
                false, new Birthday(Calendar.FEBRUARY + 1, 8), date(Calendar.FEBRUARY, 8, 4)));
        assertEquals("birthday quote", selector.select(QuoteSelector.QuoteKind.ASK,
                true, new Birthday(Calendar.FEBRUARY + 1, 8), date(Calendar.FEBRUARY, 8, 4)));
        assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK,
                true, new Birthday(Calendar.FEBRUARY + 1, 8), date(Calendar.FEBRUARY, 9, 4)));
    }

    @Test
    public void februaryTwentyNinthBirthdayMatchesOnlyOnLeapDay() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK,
                QuoteSelector.Category.PERSONAL_BIRTHDAY, "birthday quote");
        QuoteSelector selector = selector(catalog);
        Birthday birthday = new Birthday(Calendar.FEBRUARY + 1, 29);

        assertEquals("birthday quote", selector.select(QuoteSelector.QuoteKind.ASK,
                true, birthday, date(2024, Calendar.FEBRUARY, 29, 4)));
        assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK,
                true, birthday, date(2025, Calendar.FEBRUARY, 28, 4)));
        assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK,
                true, birthday, date(2025, Calendar.MARCH, 1, 4)));
    }

    private static void assertDateRange(QuoteSelector.Category category,
                                        int[][] includedDates,
                                        int[][] excludedDates) {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK, category, category.name());
        QuoteSelector selector = selector(catalog);

        for (int[] date : includedDates) {
            assertEquals(category.name(), selector.select(QuoteSelector.QuoteKind.ASK, false,
                    date(date[0], date[1], 4)));
        }
        for (int[] date : excludedDates) {
            assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK, false,
                    date(date[0], date[1], 4)));
        }
    }

    private static QuoteSelector selector(FakeCatalog catalog) {
        return new QuoteSelector(catalog, new Random(0));
    }

    private static final class FixedRandom extends Random {
        private final int value;

        FixedRandom(int value) {
            this.value = value;
        }

        @Override
        public int nextInt(int bound) {
            if (value >= bound) {
                throw new AssertionError("Requested candidate " + value + " from " + bound);
            }
            return value;
        }
    }

    private static Calendar date(int month, int day, int hour) {
        return date(2025, month, day, hour);
    }

    private static Calendar date(int year, int month, int day, int hour) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(year, month, day, hour, 0, 0);
        return date;
    }

    private static final class FakeCatalog implements QuoteSelector.QuoteCatalog {
        private final Map<String, String[]> quotes = new HashMap<>();

        void set(QuoteSelector.QuoteKind kind, QuoteSelector.Category category, String... values) {
            quotes.put(key(kind, category), values);
        }

        @Override
        public String[] getQuotes(QuoteSelector.QuoteKind kind, QuoteSelector.Category category) {
            String[] values = quotes.get(key(kind, category));
            return values == null ? new String[0] : values;
        }

        private String key(QuoteSelector.QuoteKind kind, QuoteSelector.Category category) {
            return kind.name() + "_" + category.name();
        }
    }
}
