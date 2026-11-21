package pl.lab.roombooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lab.roombooking.model.Room;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByCapacityGreaterThanEqualAndActiveTrue(int minCapacity);
    boolean existsByNameIgnoreCase(String name);
}
