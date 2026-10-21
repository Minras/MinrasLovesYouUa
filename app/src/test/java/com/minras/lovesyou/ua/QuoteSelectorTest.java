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
                QuoteSelector.QuoteKind.ASK, null, date(Calendar.JUNE, 10, 19)));
        assertEquals("tell quote", selector.select(
                QuoteSelector.QuoteKind.TELL, null, date(Calendar.JUNE, 10, 19)));
    }

    @Test
    public void ignoresEmptyOptionalCategoriesAndStillSelectsGeneralQuote() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.GENERAL, "general quote");
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.WINTER);
        QuoteSelector selector = selector(catalog);

        assertEquals("general quote", selector.select(
                QuoteSelector.QuoteKind.ASK, null, date(Calendar.JANUARY, 10, 19)));
    }

    @Test
    public void returnsEmptyStringWhenEveryApplicableCategoryIsEmpty() {
        QuoteSelector selector = selector(new FakeCatalog());

        assertEquals("", selector.select(
                QuoteSelector.QuoteKind.ASK, null, date(Calendar.JUNE, 10, 19)));
    }

    @Test
    public void includesPersonalQuotesForRecognizedAccount() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.TELL, QuoteSelector.Category.PERSONAL, "personal quote");
        QuoteSelector selector = selector(catalog);

        assertEquals("personal quote", selector.select(
                QuoteSelector.QuoteKind.TELL,
                "andrey.shchurkov@gmail.com",
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

        for (int hour = 0; hour < 24; hour++) {
            QuoteSelector.Category expectedCategory = expectedTimeCategory(hour);
            String expectedQuote = expectedCategory == null ? "" : expectedCategory.name();
            assertEquals("Unexpected time category at hour " + hour,
                    expectedQuote,
                    selector.select(QuoteSelector.QuoteKind.ASK, null,
                            date(Calendar.JUNE, 10, hour)));
        }
    }

    @Test
    public void fourAmUsesMorningCategory() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK, QuoteSelector.Category.MORNING, "morning quote");
        QuoteSelector selector = selector(catalog);

        assertEquals("morning quote", selector.select(QuoteSelector.QuoteKind.ASK, null,
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

        for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
            QuoteSelector.Category expectedCategory = expectedSeason(month);
            assertEquals("Unexpected season in month " + (month + 1), expectedCategory.name(),
                    selector.select(QuoteSelector.QuoteKind.ASK, null,
                            date(month, 10, 4)));
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
    public void personalBirthdayQuotesApplyOnlyOnFebruaryEighth() {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK,
                QuoteSelector.Category.PERSONAL_BIRTHDAY, "birthday quote");
        QuoteSelector selector = selector(catalog);

        assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK,
                "andrey.shchurkov@gmail.com", date(Calendar.FEBRUARY, 7, 4)));
        assertEquals("birthday quote", selector.select(QuoteSelector.QuoteKind.ASK,
                "andrey.shchurkov@gmail.com", date(Calendar.FEBRUARY, 8, 4)));
        assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK,
                "andrey.shchurkov@gmail.com", date(Calendar.FEBRUARY, 9, 4)));
    }

    private QuoteSelector.Category expectedTimeCategory(int hour) {
        if (hour >= 4 && hour <= 11) {
            return QuoteSelector.Category.MORNING;
        }
        if (hour >= 12 && hour <= 17) {
            return QuoteSelector.Category.DAY;
        }
        if (hour == 23 || (hour >= 0 && hour <= 3)) {
            return QuoteSelector.Category.NIGHT;
        }
        return null;
    }

    private QuoteSelector.Category expectedSeason(int month) {
        if (month == Calendar.DECEMBER || month == Calendar.JANUARY || month == Calendar.FEBRUARY) {
            return QuoteSelector.Category.WINTER;
        }
        if (month >= Calendar.MARCH && month <= Calendar.MAY) {
            return QuoteSelector.Category.SPRING;
        }
        if (month >= Calendar.JUNE && month <= Calendar.AUGUST) {
            return QuoteSelector.Category.SUMMER;
        }
        return QuoteSelector.Category.AUTUMN;
    }

    private void assertDateRange(QuoteSelector.Category category,
                                int[][] includedDates,
                                int[][] excludedDates) {
        FakeCatalog catalog = new FakeCatalog();
        catalog.set(QuoteSelector.QuoteKind.ASK, category, category.name());
        QuoteSelector selector = selector(catalog);

        for (int[] date : includedDates) {
            assertEquals(category.name(), selector.select(QuoteSelector.QuoteKind.ASK, null,
                    date(date[0], date[1], 4)));
        }
        for (int[] date : excludedDates) {
            assertEquals("", selector.select(QuoteSelector.QuoteKind.ASK, null,
                    date(date[0], date[1], 4)));
        }
    }

    private QuoteSelector selector(FakeCatalog catalog) {
        return new QuoteSelector(catalog, new Random(0));
    }

    private Calendar date(int month, int day, int hour) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(2025, month, day, hour, 0, 0);
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
