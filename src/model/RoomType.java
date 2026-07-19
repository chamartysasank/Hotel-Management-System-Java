package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Enum representing the different categories of rooms offered by the hotel.
 * Each type has a base nightly rate and a maximum guest capacity.
 */
public enum RoomType {
    SINGLE("Single", new BigDecimal("1500.00"), 1),
    DOUBLE("Double", new BigDecimal("2500.00"), 2),
    DELUXE("Deluxe", new BigDecimal("4000.00"), 3),
    SUITE("Suite", new BigDecimal("6500.00"), 4);

    private final String label;
    private final BigDecimal baseRate;
    private final int maxCapacity;

    RoomType(String label, BigDecimal baseRate, int maxCapacity) {
        this.label = label;
        this.baseRate = baseRate;
        this.maxCapacity = maxCapacity;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getNightlyRate() {
        return baseRate.setScale(2, RoundingMode.HALF_UP);
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        return label;
    }
}
