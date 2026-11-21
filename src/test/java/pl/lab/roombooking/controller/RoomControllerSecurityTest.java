package pl.lab.roombooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.lab.roombooking.dto.RoomRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/rooms powinien być dostępny publicznie bez uwierzytelnienia (HTTP 200)")
    void shouldAllowPublicAccessToGetRooms() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/rooms bez logowania powinien zwrócić HTTP 401 Unauthorized")
    void shouldRejectUnauthenticatedRoomCreation() throws Exception {
        RoomRequest request = new RoomRequest("Sala Testowa 101", "Budynek A", 30, "Projektor");

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "jan", roles = {"USER"})
    @DisplayName("POST /api/rooms z rolą USER powinien zwrócić HTTP 403 Forbidden (po wykonaniu Zadania 1)")
    void shouldForbidRoomCreationForRegularUser() throws Exception {
        // TODO (ZADANIE 4 - Testy Security):
        // Ten test przejdzie dopiero po poprawnej konfiguracji reguł RBAC w SecurityConfig!
        RoomRequest request = new RoomRequest("Sala Nieautoryzowana", "Budynek B", 15, "Tablica");

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /api/rooms z rolą ADMIN powinien utworzyć salę i zwrócić HTTP 201 Created")
    void shouldAllowRoomCreationForAdmin() throws Exception {
        RoomRequest request = new RoomRequest("Nowa Aula D1", "Budynek D", 80, "Projektor 4K");

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nowa Aula D1"))
                .andExpect(jsonPath("$.capacity").value(80));
    }
}
