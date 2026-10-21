package com.minras.lovesyou.ua;

import org.junit.Test;

import java.util.Calendar;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class QuoteSelectionBehaviorTest {

    @Test
    public void selectsOnlyNonBlankQuotesFromApplicableCategories() {
        QuoteSelector.QuoteCatalog catalog = (kind, category) -> {
            if (category == QuoteSelector.Category.GENERAL) {
                return new String[] {null, "", " \t ", "usable quote"};
            }
            if (category == QuoteSelector.Category.SUMMER) {
                return new String[] {null, "\n"};
            }
            return new String[0];
        };
        QuoteSelector selector = new QuoteSelector(catalog, new Random(0) {
            @Override
            public int nextInt(int bound) {
                return bound - 1;
            }
        });
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(2026, Calendar.JUNE, 10, 18, 0);

        assertEquals("usable quote", selector.select(
                QuoteSelector.QuoteKind.TELL, false, date));
    }
}
