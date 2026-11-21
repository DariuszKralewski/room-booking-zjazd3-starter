package pl.lab.roombooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lab.roombooking.dto.AuthUserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Weryfikacja tożsamości i ról zalogowanego użytkownika")
public class AuthController {

    @GetMapping("/me")
    @Operation(summary = "Pobierz dane aktualnie zalogowanego użytkownika (HTTP Basic)")
    public ResponseEntity<AuthUserResponse> getCurrentUser(Authentication authentication) {
        // TODO (ZADANIE 3 - Tożsamość użytkownika):
        // Przeanalizuj obiekt Authentication wstrzykiwany przez Spring Security.
        // Sprawdź, w jakiej postaci zwracane są role (np. "ROLE_ADMIN" vs "ADMIN").
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        AuthUserResponse response = new AuthUserResponse(
                authentication.getName(),
                roles,
                authentication.isAuthenticated()
        );
        return ResponseEntity.ok(response);
    }
}
