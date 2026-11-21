package pl.lab.roombooking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lab.roombooking.dto.RoomRequest;
import pl.lab.roombooking.dto.RoomResponse;
import pl.lab.roombooking.exception.ReservationConflictException;
import pl.lab.roombooking.exception.ResourceNotFoundException;
import pl.lab.roombooking.model.Room;
import pl.lab.roombooking.repository.RoomRepository;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms(Integer minCapacity) {
        List<Room> rooms = (minCapacity != null && minCapacity > 0)
                ? roomRepository.findByCapacityGreaterThanEqualAndActiveTrue(minCapacity)
                : roomRepository.findAll();
        return rooms.stream().map(RoomResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + id));
        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByNameIgnoreCase(request.name())) {
            throw new ReservationConflictException("Sala o nazwie '" + request.name() + "' już istnieje.");
        }
        Room room = new Room(
                request.name(),
                request.building(),
                request.capacity(),
                request.equipment()
        );
        return RoomResponse.fromEntity(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + id));
        room.setName(request.name());
        room.setBuilding(request.building());
        room.setCapacity(request.capacity());
        room.setEquipment(request.equipment());
        return RoomResponse.fromEntity(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono sali o ID: " + id);
        }
        roomRepository.deleteById(id);
    }
}
