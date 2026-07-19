package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a hotel room booking, including guest info, stay dates,
 * computed nights/charges, and current lifecycle status.
 */
public class Booking {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    private final String bookingId;
    private final Guest guest;
    private final int roomNumber;
    private final RoomType roomType;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int numberOfGuests;
    private final BigDecimal nightlyRate;
    private final long nights;
    private final BigDecimal roomCharge;
    private final BigDecimal tax;
    private final BigDecimal total;
    private final LocalDateTime createdAt;
    private BookingStatus status;

    public Booking(String bookingId, Guest guest, int roomNumber, RoomType roomType,
                    LocalDate checkIn, LocalDate checkOut, int numberOfGuests,
                    BigDecimal nightlyRate, BookingStatus status, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.guest = guest;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.nightlyRate = nightlyRate.setScale(2, RoundingMode.HALF_UP);
        this.nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.roomCharge = this.nightlyRate.multiply(BigDecimal.valueOf(this.nights))
                .setScale(2, RoundingMode.HALF_UP);
        this.tax = this.roomCharge.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        this.total = this.roomCharge.add(this.tax).setScale(2, RoundingMode.HALF_UP);
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Guest getGuest() {
        return guest;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public BigDecimal getNightlyRate() {
        return nightlyRate;
    }

    public long getNights() {
        return nights;
    }

    public BigDecimal getRoomCharge() {
        return roomCharge;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public static BigDecimal getTaxRate() {
        return TAX_RATE;
    }

    @Override
    public String toString() {
        return bookingId + " | Room " + roomNumber + " | " + guest.getName() + " | " + status;
    }
}
