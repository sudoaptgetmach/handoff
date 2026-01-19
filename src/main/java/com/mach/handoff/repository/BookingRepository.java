package com.mach.handoff.repository;

import com.mach.handoff.domain.bookings.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
                 SELECT COUNT(b) > 0\s
                 FROM Booking b\s
                 WHERE b.position = :position\s
                 AND (b.status = 'ATRIBUIDO'\s
                 OR b.status = 'CONFIRMADO')
                 AND (b.startTime < :endTime AND b.endTime > :startTime)
            \s""")
    boolean hasConfirmedOverlap(String position, LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
                 SELECT COUNT(b) > 0\s
                 FROM Booking b\s
                 WHERE b.user.cid = :user\s
                 AND b.position = :position\s
                 AND (b.startTime = :startTime AND b.endTime = :endTime)
            \s""")
    boolean isDuplicate(Long user, String position, LocalDateTime startTime, LocalDateTime endTime);

    List<Booking> findAllByEvent_Id(Long eventId);
}
