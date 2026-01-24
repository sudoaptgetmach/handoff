package com.mach.handoff.repository;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.events.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
             SELECT b\s
             FROM Booking b\s
             WHERE b.user.cid = :userCid\s
               AND b.event.id = :eventId\s
               AND b.id <> :excludedBookingId
            \s""")
    List<Booking> findAllOtherBookingsForEvent(
            @Param("userCid") Long userCid,
            @Param("eventId") Long eventId,
            @Param("excludedBookingId") Long excludedBookingId
    );

    List<Booking> findAllByEvent_Id(Long eventId);

    List<Booking> findAllByEvent_IdAndStatus(Long id, BookingStatus status);

    List<Booking> findAllByStatus(BookingStatus status);

    List<Booking> findByUserCidOrderByStartTimeDesc(Long userCid);

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
                 WHERE b.user.cid = :userCid\s
                 AND b.position = :position\s
                 AND (b.startTime = :startTime AND b.endTime = :endTime)
            \s""")
    boolean isDuplicate(Long userCid, String position, LocalDateTime startTime, LocalDateTime endTime);

    List<Booking> findAllActiveByEvent(Event event);
}
