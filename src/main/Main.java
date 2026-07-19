package main;

import model.*;
import repository.FileManager;
import service.HotelService;
import utility.ConsoleColors;
import utility.ConsoleUI;
import utility.InputReader;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Application entry point. Boots the console UI, wires the service and
 * repository layers together, and drives the main menu loop.
 *
 * Developer: Sasank
 */
public class Main {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Scanner scanner = new Scanner(System.in);
    private final InputReader in = new InputReader(scanner);
    private final FileManager fileManager = new FileManager("data/bookings.txt", "outputs");
    private final HotelService hotelService = new HotelService(fileManager);

    public static void main(String[] args) {
        // Force UTF-8 on stdout so output is consistent across terminals/OS.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        new Main().run();
    }

    private void run() {
        bootSequence();
        mainMenuLoop();
        farewell();
    }

    // ---------------------------------------------------------------
    // Startup
    // ---------------------------------------------------------------
    private void bootSequence() {
        ConsoleUI.loadingAnimation("Initializing Hotel Management System", 18);
        ConsoleUI.spinnerStep("Loading Rooms", 6);
        ConsoleUI.spinnerStep("Loading Booking Database", 6);
        ConsoleUI.spinnerStep("Preparing Console", 6);

        ConsoleUI.clearScreenSoft();
        ConsoleUI.printTitleScreen();
        ConsoleUI.printCurrentDateTime();

        printDashboard();
    }

    private void printDashboard() {
        int totalRooms = hotelService.listAllRooms().size();

        int vacant = 0;
        int occupied = 0;
        for (Room room : hotelService.listAllRooms()) {
            if (room.getStatus() == RoomStatus.VACANT) {
                vacant++;
            } else if (room.getStatus() == RoomStatus.OCCUPIED) {
                occupied++;
            }
        }

        int reserved = 0;
        int checkedIn = 0;
        int completed = 0;
        int cancelled = 0;
        for (Booking b : hotelService.getBookingHistory()) {
            switch (b.getStatus()) {
                case RESERVED -> reserved++;
                case CHECKED_IN -> checkedIn++;
                case COMPLETED -> completed++;
                case CANCELLED -> cancelled++;
            }
        }

        ConsoleUI.sectionHeader("HOTEL DASHBOARD");
        System.out.println("Total Rooms      : " + totalRooms);
        System.out.println("Vacant Rooms     : " + vacant);
        System.out.println("Occupied Rooms   : " + occupied);
        System.out.println();
        System.out.println("Reserved         : " + reserved);
        System.out.println("Checked In       : " + checkedIn);
        System.out.println("Completed        : " + completed);
        System.out.println("Cancelled        : " + cancelled);
        System.out.println();
    }

    private void farewell() {
        System.out.println();
        ConsoleUI.sectionHeader("Thank you for using Sasank Grand Hotel System");
        ConsoleUI.info("Goodbye, and have a wonderful day!");
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Menu loop
    // ---------------------------------------------------------------
    private void mainMenuLoop() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice;
            try {
                choice = in.readInt("Choose an option (1-11):", 1, 11);
            } catch (NoSuchElementException | IllegalStateException e) {
                // Input stream closed unexpectedly (e.g. piped input ran out,
                // or Ctrl+D/Ctrl+Z was pressed). Exit gracefully instead of
                // crashing with a stack trace.
                System.out.println();
                ConsoleUI.warn("Input stream closed. Exiting application.");
                return;
            }
            System.out.println();
            try {
                switch (choice) {
                    case 1 -> listRooms();
                    case 2 -> searchAvailableRooms();
                    case 3 -> createBookingFlow();
                    case 4 -> viewBookingFlow();
                    case 5 -> checkInFlow();
                    case 6 -> checkOutFlow();
                    case 7 -> cancelBookingFlow();
                    case 8 -> bookingHistory();
                    case 9 -> reports();
                    case 10 -> findBookingByPhoneFlow();
                    case 11 -> {
                        int exit = in.readInt("Exit Application? (1.Yes 2.No)", 1, 2);
                        if (exit == 1) {
                            running = false;
                        }
                    }
                }
            } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
                ConsoleUI.error(e.getMessage());
            } catch (Exception e) {
                ConsoleUI.error("Unexpected error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printMenu() {
        ConsoleUI.sectionHeader("MAIN MENU");
        System.out.println(ConsoleColors.BRIGHT_WHITE +
                " 1. List all rooms\n" +
                " 2. Search available rooms\n" +
                " 3. Book a room\n" +
                " 4. View booking details\n" +
                " 5. Check-in a guest\n" +
                " 6. Check-out and print bill\n" +
                " 7. Cancel a booking\n" +
                " 8. Booking history\n" +
                " 9. Reports\n" +
                " 10. Find my Booking ID (search by phone)\n" +
                " 11. Exit\n" + ConsoleColors.RESET);
    }

    // ---------------------------------------------------------------
    // Feature handlers
    // ---------------------------------------------------------------
    private void listRooms() {
        ConsoleUI.sectionHeader("All Rooms");
        List<Room> rooms = hotelService.listAllRooms();
        String[] headers = {"Room No", "Type", "Status", "Capacity", "Rate/Night"};
        List<String[]> rows = new ArrayList<>();
        for (Room r : rooms) {
            rows.add(new String[]{
                    String.valueOf(r.getRoomNumber()),
                    r.getType().getLabel(),
                    r.getStatus().toString(),
                    String.valueOf(r.getMaxCapacity()),
                    ConsoleUI.money(r.getNightlyRate())
            });
        }
        ConsoleUI.printTable(headers, rows);
    }

    private void searchAvailableRooms() {
        ConsoleUI.sectionHeader("Search Available Rooms");
        RoomType type = pickRoomType();
        LocalDate checkIn = in.readDate("Check-in date");
        LocalDate checkOut = in.readDate("Check-out date");
        if (!hotelService.isValidDateRange(checkIn, checkOut)) {
            ConsoleUI.error("Check-out date must be after check-in date.");
            return;
        }
        List<Room> free = hotelService.findAvailableRooms(type, checkIn, checkOut);
        if (free.isEmpty()) {
            ConsoleUI.warn("No " + type.getLabel() + " rooms available for those dates.");
            return;
        }
        String[] headers = {"Room No", "Type", "Capacity", "Rate/Night"};
        List<String[]> rows = new ArrayList<>();
        for (Room r : free) {
            rows.add(new String[]{
                    String.valueOf(r.getRoomNumber()), r.getType().getLabel(),
                    String.valueOf(r.getMaxCapacity()), ConsoleUI.money(r.getNightlyRate())
            });
        }
        ConsoleUI.printTable(headers, rows);
    }

    private void createBookingFlow() {
        ConsoleUI.sectionHeader("Book a Room");
        RoomType type = pickRoomType();
        LocalDate checkIn = in.readDate("Check-in date");
        LocalDate checkOut = in.readDate("Check-out date");
        if (!hotelService.isValidDateRange(checkIn, checkOut)) {
            ConsoleUI.error("Check-out date must be after check-in date.");
            return;
        }
        List<Room> free = hotelService.findAvailableRooms(type, checkIn, checkOut);
        if (free.isEmpty()) {
            ConsoleUI.warn("No rooms available. Try a different type or dates.");
            return;
        }
        String[] headers = {"Room No", "Type", "Capacity", "Rate/Night"};
        List<String[]> rows = new ArrayList<>();
        List<Integer> allowedNumbers = new ArrayList<>();
        for (Room r : free) {
            rows.add(new String[]{
                    String.valueOf(r.getRoomNumber()), r.getType().getLabel(),
                    String.valueOf(r.getMaxCapacity()), ConsoleUI.money(r.getNightlyRate())
            });
            allowedNumbers.add(r.getRoomNumber());
        }
        ConsoleUI.printTable(headers, rows);
        int roomNumber = in.readIntFromSet("Enter the room number you want to book:", allowedNumbers);

        String name = in.readNonEmpty("Guest full name:");
        String phone = in.readPhone("Guest phone number:");
        String email = in.readEmail("Guest email:");
        int numGuests = in.readInt("Number of guests staying:", 1, 10);

        Guest guest = new Guest(name, phone, email);
        Booking booking = hotelService.createBooking(guest, roomNumber, checkIn, checkOut, numGuests);
        fileManager.saveReceipt(booking);

        ConsoleUI.printBookingIdHighlight(booking.getBookingId());

        ConsoleUI.sectionHeader("BOOKING CONFIRMED");

        System.out.println("Booking ID      : " + booking.getBookingId());
        System.out.println("Guest Name      : " + booking.getGuest().getName());
        System.out.println("Room Number     : " + booking.getRoomNumber());
        System.out.println("Room Type       : " + booking.getRoomType().getLabel());
        System.out.println("Check In        : " + booking.getCheckIn());
        System.out.println("Check Out       : " + booking.getCheckOut());
        System.out.println();

        ConsoleUI.success("Receipt saved in outputs folder.");
        ConsoleUI.warn("Please save your Booking ID.");
        System.out.println();

        ConsoleUI.printReceipt(booking);
    }

    private void viewBookingFlow() {
        ConsoleUI.sectionHeader("View Booking");
        String id = in.readNonEmpty("Enter booking ID (e.g. BK1001):");
        Booking b = hotelService.findBooking(id)
                .orElseThrow(() -> new NoSuchElementException("No booking found with ID " + id));
        ConsoleUI.printReceipt(b);
    }

    private void checkInFlow() {
        ConsoleUI.sectionHeader("Check-in Guest");
        String id = in.readNonEmpty("Enter booking ID:");
        hotelService.checkIn(id);
        ConsoleUI.success("Guest checked in successfully for booking " + id + ". Enjoy your stay!");
    }

    private void checkOutFlow() {
        ConsoleUI.sectionHeader("Check-out Guest");
        String id = in.readNonEmpty("Enter booking ID:");
        Booking temp = hotelService.findBooking(id)
                .orElseThrow(() -> new NoSuchElementException("No booking found with ID " + id));

        System.out.println();
        System.out.println("Bill Summary");
        System.out.println("----------------------");
        System.out.println("Room Charge : " + ConsoleUI.money(temp.getRoomCharge()));
        System.out.println("Tax         : " + ConsoleUI.money(temp.getTax()));
        System.out.println("Total       : " + ConsoleUI.money(temp.getTotal()));
        System.out.println("----------------------");

        int confirm = in.readInt("Proceed Checkout? (1.Yes 2.No)", 1, 2);
        if (confirm == 2) {
            ConsoleUI.info("Checkout Cancelled.");
            return;
        }

        Booking b = hotelService.checkOut(id);
        fileManager.saveReceipt(b);
        ConsoleUI.success("Checked out successfully. Final bill below:");
        ConsoleUI.printReceipt(b);
    }

    private void cancelBookingFlow() {
        ConsoleUI.sectionHeader("Cancel Booking");
        String id = in.readNonEmpty("Enter booking ID:");
        int confirm = in.readInt("Are you sure? (1.Yes 2.No)", 1, 2);

        if (confirm == 1) {
            hotelService.cancelBooking(id);
            ConsoleUI.success("Booking " + id + " has been cancelled.");
        } else {
            ConsoleUI.info("Cancellation cancelled.");
        }
    }

    private void bookingHistory() {
        ConsoleUI.sectionHeader("Booking History");
        List<Booking> all = hotelService.getBookingHistory();
        if (all.isEmpty()) {
            ConsoleUI.info("No bookings have been made yet.");
            return;
        }
        String[] headers = {"Booking ID", "Guest", "Room", "Check-in", "Check-out", "Status", "Total"};
        List<String[]> rows = new ArrayList<>();
        for (Booking b : all) {
            rows.add(new String[]{
                    b.getBookingId(), b.getGuest().getName(), String.valueOf(b.getRoomNumber()),
                    b.getCheckIn().format(DATE_FMT), b.getCheckOut().format(DATE_FMT),
                    b.getStatus().toString(), ConsoleUI.money(b.getTotal())
            });
        }
        ConsoleUI.printTable(headers, rows);
    }

    private void findBookingByPhoneFlow() {
        ConsoleUI.sectionHeader("Find My Booking ID");
        String phone = in.readPhone("Enter the phone number used when booking:");
        List<Booking> matches = hotelService.findBookingsByPhone(phone);
        if (matches.isEmpty()) {
            ConsoleUI.warn("No bookings found for phone number " + phone + ".");
            return;
        }
        String[] headers = {"Booking ID", "Guest", "Room", "Check-in", "Check-out", "Status"};
        List<String[]> rows = new ArrayList<>();
        for (Booking b : matches) {
            rows.add(new String[]{
                    b.getBookingId(), b.getGuest().getName(), String.valueOf(b.getRoomNumber()),
                    b.getCheckIn().format(DATE_FMT), b.getCheckOut().format(DATE_FMT),
                    b.getStatus().toString()
            });
        }
        ConsoleUI.printTable(headers, rows);
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------
    private void reports() {
        ConsoleUI.sectionHeader("REPORTS");
        System.out.println("Total Revenue : " + ConsoleUI.money(hotelService.getTotalRevenue()));
        System.out.println();
        System.out.println("Total Bookings : " + hotelService.getBookingHistory().size());
    }

    private RoomType pickRoomType() {
        RoomType[] types = RoomType.values();
        System.out.println(ConsoleColors.BRIGHT_WHITE + "Choose a room type:" + ConsoleColors.RESET);
        for (int i = 0; i < types.length; i++) {
            System.out.println(" " + (i + 1) + ". " + types[i].getLabel() +
                    "  (Rate: " + ConsoleUI.money(types[i].getNightlyRate()) + "/night, capacity " + types[i].getMaxCapacity() + ")");
        }
        int idx = in.readInt("Enter option:", 1, types.length);
        return types[idx - 1];
    }
}
