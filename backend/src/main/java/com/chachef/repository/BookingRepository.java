package com.chachef.repository;

import com.chachef.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking,Integer> {
    public Optional<List<Booking>> findByUserId(UUID userId);
    public Optional<List<Booking>> findByChefId(UUID chefId);
    public Optional<Booking> findByBookingId(UUID bookingId);
    public void deleteByBookingId(UUID bookingId);
}
