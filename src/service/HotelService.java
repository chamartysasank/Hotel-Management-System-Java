package service;

import model.*;
import repository.FileManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Core business logic: room inventory, availability checks, booking
 * lifecycle (create, check-in, check-out, cancel), and billing.
 */
public class HotelService {

    private final Map<Integer, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();
    private final FileManager fileManager;
    private int nextBookingSeq = 1001;

    public HotelService(FileManager fileManager) {
        this.fileManager = fileManager;
        seedRooms();
        restoreBookingsFromDisk();
    }

    private void seedRooms() {
        int number = 101;
        for (int i = 0; i < 3; i++) rooms.put(number, new Room(number++, RoomType.SINGLE));
        number = 201;
        for (int i = 0; i < 3; i++) rooms.put(number, new Room(number++, RoomType.DOUBLE));
        number = 301;
        for (int i = 0; i < 2; i++) rooms.put(number, new Room(number++, RoomType.DELUXE));
        number = 401;
        for (int i = 0; i < 2; i++) rooms.put(number, new Room(number++, RoomType.SUITE));
    }

    private void restoreBookingsFromDisk() {
        List<Booking> loaded = fileManager.loadBookings();
        for (Booking b : loaded) {
            bookings.put(b.getBookingId(), b);
            int seq = extractSeq(b.getBookingId());
            if (seq >= nextBookingSeq) {
                nextBookingSeq = seq + 1;
            }
            if (b.getStatus() == BookingStatus.CHECKED_IN) {
                Room r = rooms.get(b.getRoomNumber());
                if (r != null) {
                    r.setStatus(RoomStatus.OCCUPIED);
                }
            }
        }
    }

    private int extractSeq(String bookingId) {
        try {
            return Integer.parseInt(bookingId.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 1000;
        }
    }

    // ---------------------------------------------------------------
    // Rooms
    // ---------------------------------------------------------------
    public List<Room> listAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Room> findAvailableRooms(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        List<Room> free = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.getType() != type || room.getStatus() == RoomStatus.OUT_OF_SERVICE) {
                continue;
            }
            if (isRoomFreeForRange(room.getRoomNumber(), checkIn, checkOut)) {
                free.add(room);
            }
        }
        return free;
    }

    private boolean isRoomFreeForRange(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Booking b : bookings.values()) {
            if (b.getRoomNumber() != roomNumber) continue;
            if (b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.COMPLETED) continue;
            if (rangesOverlap(checkIn, checkOut, b.getCheckIn(), b.getCheckOut())) {
                return false;
            }
        }
        return true;
    }

    private boolean rangesOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    // ---------------------------------------------------------------
    // Bookings
    // ---------------------------------------------------------------
    public boolean isValidDateRange(LocalDate checkIn, LocalDate checkOut) {
        return checkOut.isAfter(checkIn);
    }

    public Booking createBooking(Guest guest,
                             int roomNumber,
                             LocalDate checkIn,
                             LocalDate checkOut,
                             int numGuests) {

    Room room = rooms.get(roomNumber);

    if (room == null) {
        throw new IllegalArgumentException("Room does not exist.");
    }

    String bookingId = "BK" + (nextBookingSeq++);

    Booking booking = new Booking(
            bookingId,
            guest,
            roomNumber,
            room.getType(),
            checkIn,
            checkOut,
            numGuests,
            room.getNightlyRate(),
            BookingStatus.RESERVED,
            LocalDateTime.now()
    );

    bookings.put(bookingId, booking);

    persist();

    return booking;
}
    public Optional<Booking> findBooking(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public void cancelBooking(String bookingId) {
        Booking b = requireBooking(bookingId);
        if (b.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Only reserved bookings can be cancelled.");
        }
        b.setStatus(BookingStatus.CANCELLED);
        persist();
    }

    public void checkIn(String bookingId) {
        Booking b = requireBooking(bookingId);
        if (b.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Only reserved bookings can be checked in.");
        }
        Room room = rooms.get(b.getRoomNumber());
        if (room.getStatus() != RoomStatus.VACANT) {
            throw new IllegalStateException("Room is currently not vacant.");
        }
        b.setStatus(BookingStatus.CHECKED_IN);
        room.setStatus(RoomStatus.OCCUPIED);
        persist();
    }

    public Booking checkOut(String bookingId) {
        Booking b = requireBooking(bookingId);
        if (b.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Only checked-in bookings can be checked out.");
        }
        Room room = rooms.get(b.getRoomNumber());
        b.setStatus(BookingStatus.COMPLETED);
        room.setStatus(RoomStatus.VACANT);
        persist();
        return b;
    }

    private Booking requireBooking(String bookingId) {
        Booking b = bookings.get(bookingId);
        if (b == null) {
            throw new NoSuchElementException("No booking found with ID " + bookingId);
        }
        return b;
    }

    public List<Booking> getBookingHistory() {
        return new ArrayList<>(bookings.values());
    }

    private void persist() {
        fileManager.saveAllBookings(new ArrayList<>(bookings.values()));
    }
    public BigDecimal getTotalRevenue(){

    BigDecimal revenue=BigDecimal.ZERO;

    for(Booking b:bookings.values()){

        if(b.getStatus()==BookingStatus.COMPLETED){

            revenue=revenue.add(b.getTotal());

        }

    }

    return revenue;

}
public List<Booking> findBookingsByPhone(String phone){

    List<Booking> list=new ArrayList<>();

    for(Booking b:bookings.values()){

        if(b.getGuest().getPhone().equals(phone))
            list.add(b);

    }

    return list;

}
}
