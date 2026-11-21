# Scenariusz Zajęć Laboratoryjnych — Zjazd 3 (180 minut)

**Przedmiot:** Programowanie aplikacji webowych w Javie  
**Temat:** Bezpieczeństwo aplikacji webowych (Spring Security, RBAC), polityka CORS, testy automatyczne (`MockMvc`) oraz integracja z frontendem (`Fetch API`)

---

## Harmonogram bloku (3 godziny dydaktyczne / 180 min)

### 1. Wprowadzenie i uruchomienie projektu startowego (0:00 – 0:25 | 25 min)
- **Cele:** Rozpakowanie archiwum `room-booking-zjazd3-starter.zip`, weryfikacja środowiska (Java 25, Maven), przegląd zmian względem Zjazdu 2.
- **Przebieg:**
  - Uruchomienie aplikacji (`mvn spring-boot:run`).
  - Przegląd dokumentacji w **Swagger UI** (`http://localhost:8080/swagger-ui.html`) oraz bazy H2 (`/h2-console`).
  - Omówienie nowych komponentów: `SecurityConfig`, `OpenApiConfig`, `AuthController` oraz katalogu `frontend/`.

### 2. Moduł 1: Spring Security, HTTP Basic i kontrola dostępu RBAC (0:25 – 1:10 | 45 min)
- **Live coding (15 min):**
  - Omówienie architektury łańcucha filtrów (`SecurityFilterChain`), trybu bezstanowego (`SessionCreationPolicy.STATELESS`) oraz kodowania haseł `BCryptPasswordEncoder`.
  - Prezentacja endpointu `/api/auth/me` i wstrzykiwania obiektu `Authentication`.
- **Praca własna studentów (30 min):**
  - Realizacja **Zadania 1** (reguły `hasRole("ADMIN")` dla `POST/PUT/DELETE /api/rooms/**` w `SecurityConfig.java`).
  - Weryfikacja reguł dostępu przy użyciu pliku `api-tests.http` oraz kont testowych `jan`, `anna` i `admin` (**Zadanie 3**).

### 3. Moduł 2: Polityka Same-Origin Policy i konfiguracja CORS (1:10 – 1:45 | 35 min)
- **Omówienie + demonstracja (15 min):**
  - Wyjaśnienie mechanizmu CORS, nagłówków `Origin`, `Access-Control-Allow-Origin` oraz żądań wstępnych `OPTIONS` (preflight) wywoływanych przez obecność nagłówka `Authorization`.
- **Praca własna studentów (20 min):**
  - Realizacja **Zadania 2**: uruchomienie `frontend/index.html` na lokalnym serwerze HTTP (port `5500` lub `3000`), inspekcja ruchu sieciowego w DevTools oraz test blokady nieautoryzowanego originu.

### 4. Moduł 3: Testy automatyczne warstwy bezpieczeństwa i integracji (1:45 – 2:30 | 45 min)
- **Omówienie (10 min):**
  - Prezentacja adnotacji `@SpringBootTest`, `@AutoConfigureMockMvc` oraz `@WithMockUser(username = "...", roles = {...})`.
- **Praca własna studentów (35 min):**
  - Uruchomienie `mvn test` i weryfikacja testów w `RoomControllerSecurityTest`.
  - Realizacja **Zadania 4**: dopisanie testu integracyjnego w `ReservationIntegrationTest` sprawdzającego próbę anulowania cudzej rezerwacji (`403 Forbidden` dla `anna` vs `200 OK` dla `admin`).

### 5. Moduł 4: Integracja klienta Fetch API i odbiór zadań (2:30 – 3:00 | 30 min)
- **Praca własna studentów (20 min):**
  - Realizacja **Zadania 5** (`createReservation` — `POST /api/reservations`) oraz **Zadania 6** (`cancelReservation` — `PATCH /api/reservations/{id}/cancel`) w `frontend/app.js`.
  - Weryfikacja wyświetlania komunikatów błędów zgodnych ze standardem **RFC 9457 (`ProblemDetail`)** dla kodów `401`, `403` i `409`.
- **Podsumowanie i zaliczenie zjazdu (10 min):**
  - Sprawdzenie wykonanych zadań `TODO 1–6` oraz podsumowanie pełnego cyklu wytwarzania aplikacji REST API z Zjazdów 1–3.
