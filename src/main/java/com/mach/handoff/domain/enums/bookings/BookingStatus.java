package com.mach.handoff.domain.enums.bookings;

public enum BookingStatus {
    SOLICITADO,
    ATRIBUIDO,
    REJEITADO,
    MODIFICADO,
    CONFIRMADO,
    CANCELADO;

    public boolean canTransitionTo(BookingStatus nextStatus) {
        return switch (this) {
            case SOLICITADO ->
                    nextStatus == ATRIBUIDO || nextStatus == REJEITADO || nextStatus == MODIFICADO || nextStatus == CANCELADO;
            case ATRIBUIDO -> nextStatus == CONFIRMADO || nextStatus == CANCELADO || nextStatus == REJEITADO;
            case MODIFICADO -> nextStatus == ATRIBUIDO || nextStatus == REJEITADO || nextStatus == CANCELADO;
            case CONFIRMADO -> nextStatus == CANCELADO;
            default -> false;
        };
    }
}