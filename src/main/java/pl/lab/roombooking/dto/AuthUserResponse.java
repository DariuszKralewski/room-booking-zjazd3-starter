package pl.lab.roombooking.dto;

import java.util.List;

public record AuthUserResponse(
        String username,
        List<String> roles,
        boolean authenticated
) {
}
