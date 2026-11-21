package pl.lab.roombooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lab.roombooking.dto.RoomRequest;
import pl.lab.roombooking.dto.RoomResponse;
import pl.lab.roombooking.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "Zarządzanie salami dydaktycznymi")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Pobierz listę sal (publiczne)")
    public ResponseEntity<List<RoomResponse>> getAllRooms(
            @RequestParam(required = false) Integer minCapacity
    ) {
        return ResponseEntity.ok(roomService.getAllRooms(minCapacity));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz szczegóły sali po ID (publiczne)")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    @Operation(summary = "Utwórz nową salę (wymaga ROLE_ADMIN)")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse created = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj dane sali (wymaga ROLE_ADMIN)")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request
    ) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń salę (wymaga ROLE_ADMIN)")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
