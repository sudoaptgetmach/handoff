package com.mach.handoff.domain.enums.bookings;

public enum BookingStatus {
    SOLICITADA,
    ATRIBUIDA,
    REJEITADA,
    MODIFICADA,
    CONFIRMADA,
    CANCELADA;

    public boolean canTransitionTo(BookingStatus nextStatus) {
        return switch (this) {
            case SOLICITADA ->
                    nextStatus == ATRIBUIDA || nextStatus == REJEITADA || nextStatus == MODIFICADA || nextStatus == CANCELADA;
            case ATRIBUIDA -> nextStatus == CONFIRMADA || nextStatus == CANCELADA || nextStatus == REJEITADA;
            case MODIFICADA -> nextStatus == ATRIBUIDA || nextStatus == REJEITADA || nextStatus == CANCELADA;
            case CONFIRMADA -> nextStatus == CANCELADA;
            default -> false;
        };
    }
}