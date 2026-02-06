package com.mach.handoff.domain.bookings;

import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.exception.DomainException;
import com.mach.handoff.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.SOLICITADO;

    @ElementCollection
    @CollectionTable(name = "booking_staff_notes", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "note")
    private List<String> staffNotes;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void confirm() {
        transitionTo(BookingStatus.CONFIRMADO);
    }

    public void cancel() {
        transitionTo(BookingStatus.CANCELADO);
    }

    // ADMIN

    public void approve(User staff) {
        transitionTo(BookingStatus.ATRIBUIDO);

        this.reviewedBy = staff.getCid();
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject(User staff) {
        transitionTo(BookingStatus.REJEITADO);

        this.reviewedBy = staff.getCid();
        this.reviewedAt = LocalDateTime.now();
    }

    private void transitionTo(BookingStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new DomainException(
                    String.format("Transição de status inválida: Não é possível ir de %s para %s.",
                            this.status, newStatus)
            );
        }
        this.status = newStatus;
    }
}