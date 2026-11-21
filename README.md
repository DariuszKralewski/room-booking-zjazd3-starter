# Room Booking Service — Projekt Startowy (Zjazd 3)

Projekt edukacyjny realizowany w ramach przedmiotu **Programowanie aplikacji webowych w Javie** (Zjazd 3).
Aplikacja stanowi backendowy serwis REST API do zarządzania salami dydaktycznymi i ich rezerwacjami, wzbogacony o **Spring Security**, **politykę CORS**, **dokumentację OpenAPI / Swagger UI**, **testy automatyczne (MockMvc / @SpringBootTest)** oraz **klienta webowego Fetch API**.

## 1. Wymagania środowiskowe
- **Java JDK 25**
- **Apache Maven 3.9+** (lub wbudowany Maven w IntelliJ IDEA)
- Dowolny serwer statyczny dla katalogu `frontend/` (np. rozszerzenie *Live Server* w VS Code lub `npx serve frontend`)

## 2. Uruchomienie aplikacji
```bash
mvn spring-boot:run
```
Po uruchomieniu:
- REST API dostępne jest pod adresem: `http://localhost:8080/api`
- Dokumentacja Swagger UI (z obsługą Basic Auth): `http://localhost:8080/swagger-ui.html`
- Konsola bazy danych H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:roombookingdb`
  - User: `sa` (puste hasło)

## 3. Konta testowe (`InMemoryUserDetailsManager`)
| Login | Hasło | Przypisane role | Uprawnienia docelowe |
| :--- | :--- | :--- | :--- |
| `jan` | `user123` | `ROLE_USER` | Podgląd sal, tworzenie rezerwacji, anulowanie własnych rezerwacji |
| `anna` | `user123` | `ROLE_USER` | Podgląd sal, tworzenie rezerwacji, anulowanie własnych rezerwacji |
| `admin` | `admin123` | `ROLE_ADMIN`, `ROLE_USER` | Pełne zarządzanie salami (`POST/PUT/DELETE /api/rooms`) oraz wszystkimi rezerwacjami |

## 4. Struktura materiałów w repozytorium
- `INSTRUKCJA.md` — instrukcja laboratoryjna dla studentów (Zadania 1–6)
- `SCENARIUSZ_ZAJEC.md` — scenariusz prowadzenia 3-godzinnego bloku zajęć laboratoryjnych
- `room-booking-zjazd3-starter.md` — dokumentacja techniczna szkieletu aplikacji i architektury
- `api-tests.http` — gotowe żądania HTTP (Basic Auth) do testowania w IntelliJ IDEA / VS Code
- `frontend/` — panel klienta w HTML5 / CSS3 / JavaScript (Fetch API)
