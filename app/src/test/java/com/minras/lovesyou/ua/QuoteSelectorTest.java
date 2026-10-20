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
