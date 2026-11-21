package pl.lab.roombooking.dto;

import pl.lab.roombooking.model.Room;

public record RoomResponse(
        Long id,
        String name,
        String building,
        int capacity,
        String equipment,
        boolean active
) {
    public static RoomResponse fromEntity(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getBuilding(),
                room.getCapacity(),
                room.getEquipment(),
                room.isActive()
        );
    }
}
