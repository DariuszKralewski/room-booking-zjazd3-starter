package pl.lab.roombooking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.lab.roombooking.dto.ReservationRequest;
import pl.lab.roombooking.model.Room;
import pl.lab.roombooking.repository.RoomRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @WithMockUser(username = "jan", roles = {"USER"})
    @DisplayName("Powinien utworzyć rezerwację, a przy próbie kolizji zwrócić HTTP 409 Conflict (ProblemDetail)")
    void shouldDetectOverlappingReservationConflict() throws Exception {
        Room room = roomRepository.save(new Room("Sala Integracyjna Z3", "Budynek C", 20, "Monitor"));

        LocalDateTime start = LocalDateTime.now().plusDays(5).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);

        ReservationRequest firstRequest = new ReservationRequest(room.getId(), "Pierwsze spotkanie", start, end);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookedBy").value("jan"));

        // Próba rezerwacji nakładającej się czasowo (11:00 - 13:00)
        ReservationRequest overlappingRequest = new ReservationRequest(
                room.getId(),
                "Kolidujące spotkanie",
                start.plusHours(1),
                end.plusHours(1)
        );

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overlappingRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Konflikt rezerwacji lub duplikat zasobu"));
    }

    // TODO (ZADANIE 4 - Test integracyjny):
    // Dopisz test weryfikujący, że użytkownik "anna" (ROLE_USER) nie może anulować (PATCH /api/reservations/{id}/cancel)
    // rezerwacji utworzonej przez użytkownika "jan" (oczekiwany status: HTTP 403 Forbidden),
    // natomiast użytkownik "admin" (ROLE_ADMIN) może ją anulować (oczekiwany status: HTTP 200 OK, status = CANCELLED).
}
