package repository;

import model.Booking;
import model.BookingStatus;
import model.Guest;
import model.RoomType;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file I/O for the application: persisting booking history to
 * disk (so it survives between runs) and writing out receipt text files.
 * Uses a simple pipe-delimited text format, no external libraries.
 */
public class FileManager {

    private static final String FIELD_SEP = "|";
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Path dataFile;
    private final Path outputsDir;

    public FileManager(String dataFilePath, String outputsDirPath) {
        this.dataFile = Paths.get(dataFilePath);
        this.outputsDir = Paths.get(outputsDirPath);
        ensureDirectories();
    }

    private void ensureDirectories() {
        try {
            if (dataFile.getParent() != null) {
                Files.createDirectories(dataFile.getParent());
            }
            Files.createDirectories(outputsDir);
        } catch (IOException e) {
            System.err.println("Warning: could not create data/output directories - " + e.getMessage());
        }
    }

    /**
     * Persists the full booking list, overwriting the data file.
     */
    public void saveAllBookings(List<Booking> bookings) {
        try (BufferedWriter writer = Files.newBufferedWriter(dataFile)) {
            writer.write("# bookingId|guestName|phone|email|roomNumber|roomType|checkIn|checkOut|numberOfGuests|nightlyRate|status|createdAt");
            writer.newLine();
            for (Booking b : bookings) {
                writer.write(toLine(b));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    /**
     * Loads all previously saved bookings from disk. Returns an empty
     * list if the file does not yet exist or is empty.
     */
    public List<Booking> loadBookings() {
        List<Booking> result = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return result;
        }
        try (BufferedReader reader = Files.newBufferedReader(dataFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                Booking b = fromLine(line);
                if (b != null) {
                    result.add(b);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
        return result;
    }

    private String toLine(Booking b) {
        return String.join(FIELD_SEP,
                b.getBookingId(),
                b.getGuest().getName(),
                b.getGuest().getPhone(),
                b.getGuest().getEmail(),
                String.valueOf(b.getRoomNumber()),
                b.getRoomType().name(),
                b.getCheckIn().format(DATE_FMT),
                b.getCheckOut().format(DATE_FMT),
                String.valueOf(b.getNumberOfGuests()),
                b.getNightlyRate().toPlainString(),
                b.getStatus().name(),
                b.getCreatedAt().format(DATETIME_FMT)
        );
    }

    private Booking fromLine(String line) {
        try {
            String[] p = line.split("\\" + FIELD_SEP, -1);
            if (p.length < 12) {
                return null;
            }
            String bookingId = p[0];
            Guest guest = new Guest(p[1], p[2], p[3]);
            int roomNumber = Integer.parseInt(p[4]);
            RoomType type = RoomType.valueOf(p[5]);
            LocalDate checkIn = LocalDate.parse(p[6], DATE_FMT);
            LocalDate checkOut = LocalDate.parse(p[7], DATE_FMT);
            int guests = Integer.parseInt(p[8]);
            BigDecimal rate = new BigDecimal(p[9]);
            BookingStatus status = BookingStatus.valueOf(p[10]);
            LocalDateTime createdAt = LocalDateTime.parse(p[11], DATETIME_FMT);
            return new Booking(bookingId, guest, roomNumber, type, checkIn, checkOut, guests, rate, status, createdAt);
        } catch (Exception e) {
            System.err.println("Skipping corrupt booking record: " + e.getMessage());
            return null;
        }
    }

    /**
     * Writes a plain-text receipt for the booking into the outputs folder,
     * useful as proof-of-work / screenshots for GitHub submission.
     */
    public void saveReceipt(Booking b) {
        Path file = outputsDir.resolve("receipt_" + b.getBookingId() + ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("========================================\n");
            writer.write("        SASANK GRAND HOTEL\n");
            writer.write("        Official Booking Receipt\n");
            writer.write("========================================\n");
            writer.write("Booking ID   : " + b.getBookingId() + "\n");
            writer.write("Guest Name   : " + b.getGuest().getName() + "\n");
            writer.write("Phone        : " + b.getGuest().getPhone() + "\n");
            writer.write("Email        : " + b.getGuest().getEmail() + "\n");
            writer.write("Room Number  : " + b.getRoomNumber() + "\n");
            writer.write("Room Type    : " + b.getRoomType().getLabel() + "\n");
            writer.write("Check-in     : " + b.getCheckIn().format(DATE_FMT) + "\n");
            writer.write("Check-out    : " + b.getCheckOut().format(DATE_FMT) + "\n");
            writer.write("Nights       : " + b.getNights() + "\n");
            writer.write("Status       : " + b.getStatus() + "\n");
            writer.write("----------------------------------------\n");
            writer.write("Nightly Rate : Rs " + b.getNightlyRate() + "\n");
            writer.write("Room Charge  : Rs " + b.getRoomCharge() + "\n");
            writer.write("Tax (10%)    : Rs " + b.getTax() + "\n");
            writer.write("----------------------------------------\n");
            writer.write("TOTAL AMOUNT : Rs " + b.getTotal() + "\n");
            writer.write("========================================\n");
            writer.write("Thank you for choosing Sasank Grand Hotel.\n");
        } catch (IOException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
        }
    }
}
