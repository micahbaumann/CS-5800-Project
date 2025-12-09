package com.chachef.repository;

import com.chachef.entity.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<List<Booking>> findByUser_UserId(UUID userId);
    Optional<List<Booking>> findByChef_ChefId(UUID chefId);
    Optional<Booking> findByBookingId(UUID bookingId);
    @Transactional
    void deleteByBookingId(UUID bookingId);
}
