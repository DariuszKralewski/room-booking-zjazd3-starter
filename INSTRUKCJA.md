# Instrukcja Laboratoryjna — Zjazd 3

## Cel laboratorium
Celem zajęć jest zabezpieczenie aplikacji **Room Booking Service** za pomocą **Spring Security**, skonfigurowanie mechanizmu **CORS** dla zewnętrznego frontendu (HTML/JS oraz Next.js), napisanie testów automatycznych weryfikujących uprawnienia oraz dokończenie integracji warstwy klienckiej opartej na **Fetch API**.

---

## Zadanie 1: Konfiguracja reguł dostępu RBAC (`SecurityConfig.java`)
Otwórz klasę `pl.lab.roombooking.config.SecurityConfig` i zlokalizuj komentarz `// TODO (ZADANIE 1 - RBAC)`.

Skonfiguruj `authorizeHttpRequests` w taki sposób, aby:
1. Przeglądanie sal (`GET /api/rooms/**`) było dostępne publicznie dla wszystkich użytkowników (`permitAll()`).
2. Tworzenie, edycja i usuwanie sal (`POST /api/rooms/**`, `PUT /api/rooms/**`, `DELETE /api/rooms/**`) było dostępne **wyłącznie** dla użytkowników posiadających rolę `ADMIN` (`hasRole("ADMIN")`).
3. Endpointy `/api/reservations/**` oraz `/api/auth/me` wymagały zalogowania (`authenticated()`).

**Wskazówka:** Pamiętaj o kolejności reguł w Spring Security — reguły bardziej szczegółowe umieszczamy przed regułami ogólnymi (`anyRequest()`).

---

## Zadanie 2: Konfiguracja polityki CORS (`SecurityConfig.java`)
Przeanalizuj metodę `corsConfigurationSource()` w `SecurityConfig.java` (`// TODO (ZADANIE 2 - CORS)`).
1. Uruchom plik `frontend/index.html` za pomocą lokalnego serwera HTTP (np. Live Server na porcie `5500` lub `npx serve frontend -p 3000`).
2. Sprawdź w narzędziach deweloperskich przeglądarki (zakładka *Network*), jak przeglądarka wysyła żądanie wstępne `OPTIONS` (preflight) przed właściwym żądaniem `GET /api/auth/me` z nagłówkiem `Authorization`.
3. Zweryfikuj, co stanie się po usunięciu portu frontendu z listy `setAllowedOrigins(...)`.

---

## Zadanie 3: Weryfikacja tożsamości i powiązanie z rezerwacjami (`AuthController.java`)
1. Przeanalizuj `AuthController` (`GET /api/auth/me`) oraz sposób przekazywania obiektu `Authentication` do metod kontrolerów (`ReservationController`).
2. Przetestuj endpointy korzystając z pliku `api-tests.http` lub Swagger UI (`/swagger-ui.html` -> przycisk **Authorize**).
3. Zwróć uwagę, w jaki sposób `ReservationService#cancelReservation` sprawdza, czy użytkownik jest właścicielem rezerwacji lub administratorem, oraz jaki obiekt błędu zwraca `GlobalExceptionHandler` w przypadku naruszenia tej zasady (HTTP 403 `ProblemDetail`).

---

## Zadanie 4: Testy automatyczne z `MockMvc` i `@WithMockUser`
Uruchom testy poleceniem:
```bash
mvn test
```
1. Zwróć uwagę na test `shouldForbidRoomCreationForRegularUser()` w klasie `RoomControllerSecurityTest` — zacznie on przechodzić dopiero po poprawnym wykonaniu **Zadania 1**.
2. W klasie `ReservationIntegrationTest` dopisz brakujący test integracyjny (oznaczony jako `// TODO (ZADANIE 4 - Test integracyjny)`), który:
   - tworzy rezerwację jako użytkownik `jan` (`ROLE_USER`),
   - próbuje anulować tę rezerwację (`PATCH /api/reservations/{id}/cancel`) jako użytkownik `anna` (`ROLE_USER`) i oczekuje statusu `403 Forbidden`,
   - anuluje tę samą rezerwację jako `admin` (`ROLE_ADMIN`) i oczekuje statusu `200 OK` oraz `status = CANCELLED`.

---

## Zadanie 5: Integracja frontendu — tworzenie rezerwacji (`frontend/app.js`)
Otwórz plik `frontend/app.js` i uzupełnij funkcję **`createReservation(event)`** (`// TODO (ZADANIE 5 - Fetch API POST)`):
1. Odczytaj wartości z pól formularza `#create-reservation-form` (`reservation-room-id` jako `Number`, `reservation-title`, `reservation-start`, `reservation-end`).
2. Wyślij żądanie `POST` pod adres `${API_BASE_URL}/reservations` z nagłówkami `getHeaders(true)` oraz ciałem `JSON.stringify(payload)`.
3. Obsłuż odpowiedź przy pomocy funkcji `handleApiResponse(response)`, wyświetl komunikat sukcesu (`showAlert(...)`) i odśwież tabelę wywołując `loadReservations()`.

---

## Zadanie 6: Integracja frontendu — anulowanie rezerwacji (`frontend/app.js`)
W pliku `frontend/app.js` uzupełnij funkcję **`cancelReservation(id)`** (`// TODO (ZADANIE 6 - Fetch API PATCH)`):
1. Wyślij żądanie `PATCH` pod adres `${API_BASE_URL}/reservations/${id}/cancel` z nagłówkami `getHeaders()`.
2. Obsłuż odpowiedź przez `handleApiResponse(response)` i odśwież listę rezerwacji wywołując `loadReservations()`.
3. Przetestuj w przeglądarce próbę anulowania rezerwacji należącej do `admin` będąc zalogowanym jako `jan` — zweryfikuj wyświetlenie błędu `403 Forbidden` pochodzącego z `ProblemDetail`.
