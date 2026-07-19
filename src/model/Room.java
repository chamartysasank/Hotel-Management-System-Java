package model;

import java.math.BigDecimal;

/**
 * Represents a single hotel room.
 * Demonstrates encapsulation - fields are private, accessed via getters/setters.
 */
public class Room {
    private final int roomNumber;
    private final RoomType type;
    private RoomStatus status;

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = RoomStatus.VACANT;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public BigDecimal getNightlyRate() {
        return type.getNightlyRate();
    }

    public int getMaxCapacity() {
        return type.getMaxCapacity();
    }

    @Override
    public String toString() {
        return "Room{" + roomNumber + ", " + type + ", " + status + "}";
    }
}
