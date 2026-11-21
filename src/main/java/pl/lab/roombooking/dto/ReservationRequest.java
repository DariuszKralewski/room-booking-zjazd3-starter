package pl.lab.roombooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequest(
        @NotNull(message = "Identyfikator sali jest wymagany")
        Long roomId,

        @NotBlank(message = "Tytuł spotkania/zajęć jest wymagany")
        String title,

        @NotNull(message = "Czas rozpoczęcia jest wymagany")
        @Future(message = "Czas rozpoczęcia musi być w przyszłości")
        LocalDateTime startTime,

        @NotNull(message = "Czas zakończenia jest wymagany")
        @Future(message = "Czas zakończenia musi być w przyszłości")
        LocalDateTime endTime
) {
}
