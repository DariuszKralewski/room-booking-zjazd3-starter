package pl.lab.roombooking.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.lab.roombooking.model.Reservation;
import pl.lab.roombooking.model.Room;
import pl.lab.roombooking.repository.ReservationRepository;
import pl.lab.roombooking.repository.RoomRepository;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDatabase(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        return args -> {
            if (roomRepository.count() == 0) {
                Room aula = roomRepository.save(new Room(
                        "Aula A1",
                        "Wydział Zarządzania - Budynek Główny",
                        120,
                        "Projektor 4K, nagłośnienie, tablica interaktywna"
                ));
                Room lab = roomRepository.save(new Room(
                        "Sala Komputerowa 204",
                        "Wydział Zarządzania - Skrzydło B",
                        24,
                        "24 stacje robocze, rzutnik, Wi-Fi"
                ));
                Room conf = roomRepository.save(new Room(
                        "Sala Seminaryjna C3",
                        "Wydział Zarządzania - Budynek Główny",
                        16,
                        "Monitor 75 cali, wideokonferencje"
                ));

                LocalDateTime tomorrowMorning = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
                reservationRepository.save(new Reservation(
                        lab,
                        "jan",
                        "Laboratorium: Programowanie aplikacji webowych w Javie",
                        tomorrowMorning,
                        tomorrowMorning.plusHours(3)
                ));
                reservationRepository.save(new Reservation(
                        aula,
                        "admin",
                        "Wykład otwarty: Architektura systemów REST API",
                        tomorrowMorning.plusHours(4),
                        tomorrowMorning.plusHours(6)
                ));
            }
        };
    }
}
