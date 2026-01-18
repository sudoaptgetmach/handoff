package com.mach.handoff.repository;

import com.mach.handoff.domain.bookings.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
