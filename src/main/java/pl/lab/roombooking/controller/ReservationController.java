package pl.lab.roombooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.lab.roombooking.dto.ReservationRequest;
import pl.lab.roombooking.dto.ReservationResponse;
import pl.lab.roombooking.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Rezerwacje sal dydaktycznych")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "Pobierz listę rezerwacji (wymaga zalogowania)")
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @RequestParam(required = false) Long roomId
    ) {
        return ResponseEntity.ok(reservationService.getReservations(roomId));
    }

    @GetMapping("/my")
    @Operation(summary = "Pobierz rezerwacje zalogowanego użytkownika")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Authentication authentication) {
        return ResponseEntity.ok(reservationService.getReservationsForUser(authentication.getName()));
    }

    @PostMapping
    @Operation(summary = "Utwórz nową rezerwację")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication
    ) {
        ReservationResponse created = reservationService.createReservation(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Anuluj rezerwację (zmiana statusu na CANCELLED — właściciel lub ADMIN)")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(reservationService.cancelReservation(id, authentication.getName(), isAdmin));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Trwale usuń rezerwację (właściciel lub ADMIN)")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        reservationService.deleteReservation(id, authentication.getName(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}
