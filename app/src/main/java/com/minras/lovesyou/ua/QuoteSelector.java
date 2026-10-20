package com.minras.lovesyou.ua;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Selects a quote from the categories applicable to a request. */
final class QuoteSelector {

    interface QuoteCatalog {
        String[] getQuotes(QuoteKind kind, Category category);
    }

    enum QuoteKind {
        ASK,
        TELL
    }

    enum Category {
        GENERAL,
        PERSONAL,
        PERSONAL_BIRTHDAY,
        MORNING,
        DAY,
        NIGHT,
        WINTER,
        SPRING,
        SUMMER,
        AUTUMN,
        NEWYEAR,
        VALENTINES,
        HALLOWEEN,
        XMAS
    }

    private final QuoteCatalog catalog;
    private final Random random;

    QuoteSelector(QuoteCatalog catalog) {
        this(catalog, new Random());
    }

    QuoteSelector(QuoteCatalog catalog, Random random) {
        this.catalog = catalog;
        this.random = random;
    }

    String select(QuoteKind kind, String accountEmail, Calendar date) {
        List<String> candidates = new ArrayList<>();
        addQuotes(candidates, kind, Category.GENERAL);

        if (isPersonalAccount(accountEmail)) {
            addQuotes(candidates, kind, Category.PERSONAL);
            if (date.get(Calendar.MONTH) == Calendar.FEBRUARY
                    && date.get(Calendar.DAY_OF_MONTH) == 8) {
                addQuotes(candidates, kind, Category.PERSONAL_BIRTHDAY);
            }
        }

        Category timeCategory = timeCategory(date.get(Calendar.HOUR_OF_DAY));
        if (timeCategory != null) {
            addQuotes(candidates, kind, timeCategory);
        }

        Category season = seasonCategory(date.get(Calendar.MONTH) + 1);
        if (season != null) {
            addQuotes(candidates, kind, season);
        }

        for (Category holiday : holidayCategories(
                date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH) + 1)) {
            addQuotes(candidates, kind, holiday);
        }

        if (candidates.isEmpty()) {
            return "";
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private void addQuotes(List<String> candidates, QuoteKind kind, Category category) {
        String[] quotes = catalog.getQuotes(kind, category);
        if (quotes == null) {
            return;
        }
        for (String quote : quotes) {
            if (quote != null && !quote.trim().isEmpty()) {
                candidates.add(quote);
            }
        }
    }

    private boolean isPersonalAccount(String accountEmail) {
        return "ol.zvereva@gmail.com".equals(accountEmail)
                || "andrey.shchurkov@gmail.com".equals(accountEmail);
    }

    private Category timeCategory(int hour) {
        if (hour >= 5 && hour <= 11) {
            return Category.MORNING;
        }
        if (hour >= 12 && hour <= 17) {
            return Category.DAY;
        }
        if (hour == 23 || (hour >= 0 && hour <= 3)) {
            return Category.NIGHT;
        }
        return null;
    }

    private Category seasonCategory(int month) {
        switch (month) {
            case 1:
            case 2:
            case 12:
                return Category.WINTER;
            case 3:
            case 4:
            case 5:
                return Category.SPRING;
            case 6:
            case 7:
            case 8:
                return Category.SUMMER;
            case 9:
            case 10:
            case 11:
                return Category.AUTUMN;
            default:
                return null;
        }
    }

    private List<Category> holidayCategories(int day, int month) {
        List<Category> categories = new ArrayList<>();
        if ((month == 10 && day == 31) || (month == 11 && day == 1)) {
            Collections.addAll(categories,
                    Category.HALLOWEEN, Category.HALLOWEEN, Category.HALLOWEEN);
        }
        if (month == 12 && day >= 20 && day <= 27) {
            Collections.addAll(categories, Category.XMAS, Category.XMAS, Category.XMAS);
        }
        if ((month == 1 && day >= 1 && day <= 7) || (month == 12 && day > 24)) {
            Collections.addAll(categories,
                    Category.NEWYEAR, Category.NEWYEAR, Category.NEWYEAR);
        }
        if (month == 2 && day >= 13 && day <= 15) {
            Collections.addAll(categories,
                    Category.VALENTINES, Category.VALENTINES, Category.VALENTINES);
        }
        return categories;
    }
}
