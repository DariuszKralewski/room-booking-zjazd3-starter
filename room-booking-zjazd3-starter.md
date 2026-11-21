# Dokumentacja Techniczna Szkieletu — `room-booking-zjazd3-starter`

## 1. Stos technologiczny
- **Język:** Java 25
- **Framework:** Spring Boot 4.1.3 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-starter-security`)
- **Baza danych:** H2 Database (in-memory, `jdbc:h2:mem:roombookingdb`)
- **Dokumentacja API:** Springdoc OpenAPI 3.2.0 (`springdoc-openapi-starter-webmvc-ui`)
- **Testy:** JUnit 5, Spring MockMvc, `spring-security-test`
- **Frontend:** Vanilla HTML5 / CSS3 / ES6+ (`Fetch API`)

## 2. Struktura pakietów (`pl.lab.roombooking`)
```text
src/main/java/pl/lab/roombooking/
├── RoomBookingApplication.java          # Punkt startowy aplikacji Spring Boot
├── config/
│   ├── DataInitializer.java             # Dane startowe (3 sale, 2 rezerwacje)
│   ├── OpenApiConfig.java               # Konfiguracja Swagger UI + SecurityScheme (basicAuth)
│   └── SecurityConfig.java              # Łańcuch filtrów Security, CORS, użytkownicy InMemory [TODO 1, 2]
├── controller/
│   ├── AuthController.java              # GET /api/auth/me [TODO 3]
│   ├── RoomController.java              # CRUD /api/rooms
│   └── ReservationController.java       # Operacje /api/reservations + PATCH /{id}/cancel
├── dto/
│   ├── AuthUserResponse.java            # Rekord odpowiedzi z /api/auth/me
│   ├── RoomRequest.java                 # DTO żądania utworzenia/edycji sali
│   ├── RoomResponse.java                # DTO odpowiedzi sali
│   ├── ReservationRequest.java          # DTO żądania rezerwacji
│   └── ReservationResponse.java         # DTO odpowiedzi rezerwacji (z polem status)
├── exception/
│   ├── ForbiddenOperationException.java # Wyjątek naruszenia własności zasobu (HTTP 403)
│   ├── GlobalExceptionHandler.java      # Globalna obsługa błędów RFC 9457 ProblemDetail
│   ├── ReservationConflictException.java# Wyjątek kolizji terminów / duplikatu (HTTP 409)
│   └── ResourceNotFoundException.java   # Wyjątek braku zasobu (HTTP 404)
├── model/
│   ├── Reservation.java                 # Encja JPA rezerwacji
│   ├── ReservationStatus.java           # Enum: CONFIRMED, CANCELLED
│   └── Room.java                        # Encja JPA sali dydaktycznej
├── repository/
│   ├── ReservationRepository.java       # Zapytania JPA + wykrywanie kolizji dla statusu CONFIRMED
│   └── RoomRepository.java              # Repozytorium sal
└── service/
    ├── ReservationService.java          # Logika biznesowa rezerwacji i kontrola uprawnień
    └── RoomService.java                 # Logika biznesowa sal
```

## 3. Mapa zadań (`TODO`) w projekcie
1. `SecurityConfig.java` — reguły autoryzacji `requestMatchers` dla ról `ADMIN` i `USER` (**Zadanie 1**).
2. `SecurityConfig.java` — weryfikacja konfiguracji CORS dla portów klienckich (**Zadanie 2**).
3. `AuthController.java` — analiza obiektu `Authentication` i mapowania ról (**Zadanie 3**).
4. `RoomControllerSecurityTest.java` & `ReservationIntegrationTest.java` — testy `MockMvc` i test integracyjny anulowania rezerwacji (**Zadanie 4**).
5. `frontend/app.js` — implementacja `createReservation()` metodą `POST` (**Zadanie 5**).
6. `frontend/app.js` — implementacja `cancelReservation()` metodą `PATCH` (**Zadanie 6**).
