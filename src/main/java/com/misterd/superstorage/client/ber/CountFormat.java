package com.misterd.superstorage.client.ber;

import com.misterd.superstorage.util.FluidText;

public final class CountFormat {
    private static final String[] UNITS = {"K", "M", "B", "T"};
    public static final String INFINITE = "\u221E";

    private CountFormat() {
    }

    public static String items(long count) {
        return compact(count);
    }

    public static String buckets(long millibuckets) {
        long buckets = millibuckets / 1000;
        return (buckets < 1000 ? FluidText.buckets(millibuckets) : compact(buckets)) + " B";
    }

    private static String compact(long value) {
        if (value < 1000) {
            return Long.toString(value);
        }
        double scaled = value;
        int unit = -1;
        while (scaled >= 1000 && unit < UNITS.length - 1) {
            scaled /= 1000;
            unit++;
        }
        String number = scaled >= 100 ? String.format("%.0f", Math.floor(scaled)) : String.format("%.1f", Math.floor(scaled * 10) / 10);
        if (number.endsWith(".0")) {
            number = number.substring(0, number.length() - 2);
        }
        return number + UNITS[unit];
    }
}