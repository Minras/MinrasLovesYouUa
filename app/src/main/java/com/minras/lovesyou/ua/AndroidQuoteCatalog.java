package com.minras.lovesyou.ua;

import android.content.res.Resources;

/** Resolves quote categories to their statically declared Android string arrays. */
final class AndroidQuoteCatalog implements QuoteSelector.QuoteCatalog {

    private final Resources resources;

    AndroidQuoteCatalog(Resources resources) {
        this.resources = resources;
    }

    @Override
    public String[] getQuotes(QuoteSelector.QuoteKind kind, QuoteSelector.Category category) {
        int resourceId = resourceId(kind, category);
        if (resourceId == 0) {
            return new String[0];
        }
        return resources.getStringArray(resourceId);
    }

    private int resourceId(QuoteSelector.QuoteKind kind, QuoteSelector.Category category) {
        switch (kind) {
            case ASK:
                switch (category) {
                    case GENERAL: return R.array.ask_array;
                    case PERSONAL: return R.array.ask_array_personal;
                    case PERSONAL_BIRTHDAY: return R.array.ask_array_personal_birthday;
                    case MORNING: return R.array.ask_array_morning;
                    case DAY: return R.array.ask_array_day;
                    case NIGHT: return R.array.ask_array_night;
                    case WINTER: return R.array.ask_array_winter;
                    case SPRING: return R.array.ask_array_spring;
                    case SUMMER: return R.array.ask_array_summer;
                    case AUTUMN: return R.array.ask_array_autumn;
                    case NEWYEAR: return R.array.ask_array_newyear;
                    case VALENTINES: return R.array.ask_array_valentines;
                    case HALLOWEEN: return R.array.ask_array_halloween;
                    case XMAS: return R.array.ask_array_xmas;
                }
                break;
            case TELL:
                switch (category) {
                    case GENERAL: return R.array.tell_array;
                    case PERSONAL: return R.array.tell_array_personal;
                    case PERSONAL_BIRTHDAY: return R.array.tell_array_personal_birthday;
                    case MORNING: return R.array.tell_array_morning;
                    case DAY: return R.array.tell_array_day;
                    case NIGHT: return R.array.tell_array_night;
                    case WINTER: return R.array.tell_array_winter;
                    case SPRING: return R.array.tell_array_spring;
                    case SUMMER: return R.array.tell_array_summer;
                    case AUTUMN: return R.array.tell_array_autumn;
                    case NEWYEAR: return R.array.tell_array_newyear;
                    case VALENTINES: return R.array.tell_array_valentines;
                    case HALLOWEEN: return R.array.tell_array_halloween;
                    case XMAS: return R.array.tell_array_xmas;
                }
                break;
        }
        return 0;
    }
}
