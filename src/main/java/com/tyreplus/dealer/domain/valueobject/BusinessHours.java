package com.tyreplus.dealer.domain.valueobject;

import java.time.LocalTime;

/**
 * Value Object representing Business Hours.
 * Immutable record following DDD principles.
 */
public record BusinessHours(
        LocalTime openingTime,
        LocalTime closingTime,
        boolean isOpenOnWeekends
) {
    public BusinessHours {
        if (openingTime == null) {
            throw new IllegalArgumentException("Opening time cannot be null");
        }
        if (closingTime == null) {
            throw new IllegalArgumentException("Closing time cannot be null");
        }
        if (closingTime.isBefore(openingTime) || closingTime.equals(openingTime)) {
            throw new IllegalArgumentException("Closing time must be after opening time");
        }
    }

    public boolean isOpenAt(LocalTime time) {
        return !time.isBefore(openingTime) && !time.isAfter(closingTime);
    }
}

