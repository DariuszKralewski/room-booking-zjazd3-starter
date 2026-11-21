package pl.lab.roombooking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lab.roombooking.dto.ReservationRequest;
import pl.lab.roombooking.dto.ReservationResponse;
import pl.lab.roombooking.exception.ForbiddenOperationException;
import pl.lab.roombooking.exception.ReservationConflictException;
import pl.lab.roombooking.exception.ResourceNotFoundException;
import pl.lab.roombooking.model.Reservation;
import pl.lab.roombooking.model.ReservationStatus;
import pl.lab.roombooking.model.Room;
import pl.lab.roombooking.repository.ReservationRepository;
import pl.lab.roombooking.repository.RoomRepository;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(Long roomId) {
        List<Reservation> list = (roomId != null)
                ? reservationRepository.findByRoomIdOrderByStartTimeAsc(roomId)
                : reservationRepository.findAll();
        return list.stream().map(ReservationResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsForUser(String username) {
        return reservationRepository.findByBookedByOrderByStartTimeAsc(username)
                .stream()
                .map(ReservationResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, String username) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException("Czas zakończenia rezerwacji musi być późniejszy niż czas rozpoczęcia.");
        }

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + request.roomId()));

        if (!room.isActive()) {
            throw new ReservationConflictException("Sala '" + room.getName() + "' jest obecnie wyłączona z rezerwacji.");
        }

        boolean conflict = reservationRepository.existsOverlappingReservation(
                room.getId(),
                request.startTime(),
                request.endTime()
        );
        if (conflict) {
            throw new ReservationConflictException(
                    "Sala '" + room.getName() + "' jest już zarezerwowana w wybranym przedziale czasowym."
            );
        }

        Reservation reservation = new Reservation(
                room,
                username,
                request.title(),
                request.startTime(),
                request.endTime()
        );
        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse cancelReservation(Long reservationId, String currentUsername, boolean isAdmin) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono rezerwacji o ID: " + reservationId));

        if (!isAdmin && !reservation.getBookedBy().equals(currentUsername)) {
            throw new ForbiddenOperationException(
                    "Użytkownik '" + currentUsername + "' nie może anulować cudzej rezerwacji."
            );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }

    @Transactional
    public void deleteReservation(Long reservationId, String currentUsername, boolean isAdmin) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono rezerwacji o ID: " + reservationId));

        if (!isAdmin && !reservation.getBookedBy().equals(currentUsername)) {
            throw new ForbiddenOperationException(
                    "Użytkownik '" + currentUsername + "' nie może usunąć cudzej rezerwacji."
            );
        }

        reservationRepository.delete(reservation);
    }
}
