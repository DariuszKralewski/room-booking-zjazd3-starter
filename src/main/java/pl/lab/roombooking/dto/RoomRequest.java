package pl.lab.roombooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RoomRequest(
        @NotBlank(message = "Nazwa sali jest wymagana")
        String name,

        @NotBlank(message = "Budynek jest wymagany")
        String building,

        @Min(value = 1, message = "Pojemność sali musi wynosić co najmniej 1")
        int capacity,

        String equipment
) {
}
