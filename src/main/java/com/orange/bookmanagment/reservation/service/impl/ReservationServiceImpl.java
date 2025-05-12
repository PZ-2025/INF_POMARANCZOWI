package com.orange.bookmanagment.reservation.service.impl;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.repository.LoanRepository;
import com.orange.bookmanagment.reservation.api.ReservationExternalService;
import com.orange.bookmanagment.reservation.api.dto.ReservationExternalDto;
import com.orange.bookmanagment.reservation.exception.ReservationNotFoundException;
import com.orange.bookmanagment.reservation.service.mapper.ReservationInternalMapper;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.exception.BookNotAvailableException;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.shared.enums.LoanStatus;
import com.orange.bookmanagment.shared.enums.ReservationStatus;
import com.orange.bookmanagment.reservation.repository.ReservationRepository;
import com.orange.bookmanagment.reservation.service.ReservationService;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * ReservationServiceImpl is an implementation of the ReservationService interface.
 * It provides methods to create reservations and check the status of reservations.
 */
@Service
@RequiredArgsConstructor
class ReservationServiceImpl implements ReservationService, ReservationExternalService {
    private final ReservationRepository reservationRepository;
    private final BookExternalService bookExternalService;
    private final ReservationInternalMapper reservationInternalMapper;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Reservation createReservation(long bookId, long userId) {
        BookStatus bookStatus = bookExternalService.getBookStatusForExternal(bookId);

        if (bookStatus == BookStatus.BORROWED) {
             throw new BookNotAvailableException("This book is currently borrowed and cannot be reserved");
        } else if (bookStatus == BookStatus.LOST) {
            throw new BookNotAvailableException("This book is currently reported as lost and cannot be reserved");
        }

        // Sprawdź czy użytkownik nie ma już aktywnej rezerwacji na tę książkę
        if (isBookReservedForUser(bookId, userId)) {
            throw new BookAlreadyReservedException("You already have an active reservation for this book");
        }

        final int queuePosition = countActiveReservations(bookId) + 1;

        Reservation reservation = new Reservation(
                bookId,
                userId,
                ReservationStatus.PENDING,
                queuePosition
        );

        if (bookStatus == BookStatus.AVAILABLE) {
            reservation.setStatus(ReservationStatus.READY);

            // Aktualizuj status książki przez API zewnętrzne
            bookExternalService.updateBookStatus(bookId, BookStatus.RESERVED);
        }

        return reservationRepository.saveReservation(reservation);
    }

    @Override
    @Transactional
    public Reservation cancelReservation(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return reservation;
        }

        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.READY) {
            throw new IllegalStateException("Cannot cancel a reservation that is not active");
        }

        final long bookId = reservation.getBookId();

        if (reservation.getStatus() == ReservationStatus.READY) {
            Optional<Reservation> nextReservation = reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(
                    bookId, ReservationStatus.PENDING);

            if (nextReservation.isPresent()) {
                // Oznacz następną rezerwację jako gotową do odbioru
                nextReservation.get().setStatus(ReservationStatus.READY);
                reservationRepository.saveReservation(nextReservation.get());
            } else {
                // Brak innych rezerwacji, oznacz książkę jako dostępną
                bookExternalService.updateBookStatus(bookId, BookStatus.AVAILABLE);
            }
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        // Aktualizuj pozycje w kolejce pozostałych rezerwacji
        updateQueuePositions(bookId);

        return reservationRepository.saveReservation(reservation);
    }

    @Override
    @Transactional
    public boolean processReturnedBook(long bookId) {
        Optional<Reservation> nextReservation = reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(
                bookId, ReservationStatus.PENDING);

        if (nextReservation.isPresent()) {
            Reservation reservation = nextReservation.get();

            // Oznacz rezerwację jako gotową do odbioru
            reservation.setStatus(ReservationStatus.READY);
            reservationRepository.saveReservation(reservation);

            // Zaktualizuj status książki
            bookExternalService.updateBookStatus(bookId, BookStatus.RESERVED);

            // Zaktualizuj pozycje w kolejce
            updateQueuePositions(bookId);

            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
    }

    @Override
    @Transactional
    public List<Reservation> getUserReservations(long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);

        for (Reservation reservation : reservations) {
            if ((reservation.getStatus() == ReservationStatus.READY || reservation.getStatus() == ReservationStatus.PENDING)
                    && reservation.getExpiresAt() != null
                    && reservation.getExpiresAt().isBefore(Instant.now())) {

                // Wygaszona rezerwacja
                reservation.setStatus(ReservationStatus.EXPIRED);
                reservationRepository.saveReservation(reservation);

                // Aktualizacja kolejki
                updateQueuePositions(reservation.getBookId());

                // Jeśli rezerwacja była READY, nowa osoba dostaje READY
                if (reservation.getStatus() == ReservationStatus.READY) {
                    reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(
                            reservation.getBookId(), ReservationStatus.PENDING
                    ).ifPresent(next -> {
                        next.setStatus(ReservationStatus.READY);
                        reservationRepository.saveReservation(next);
                    });
                }
            }
        }

        return reservations;
    }

    @Override
    public List<Reservation> getActiveUserReservations(long userId) {
        return reservationRepository.findByUserIdAndStatusInOrderByQueuePosition(
                userId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    @Override
    public List<Reservation> getBookReservations(long bookId) {
        return reservationRepository.findByBookId(bookId);
    }

    @Override
    public List<Reservation> getActiveBookReservations(long bookId) {
        return reservationRepository.findByBookIdAndStatusInOrderByQueuePosition(
                bookId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    /**
     * Checks if a book is reserved for a specific user.
     *
     * @param bookId the book to check
     * @param userId the user to check
     * @return true if the book is reserved for the user, false otherwise
     */
    public boolean isBookReservedForUser(Long bookId, Long userId) {
        return reservationRepository.existsByBookIdAndUserIdAndStatusIn(
                bookId, userId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    @Override
    @Transactional
    public ReservationExternalDto completeReservation(long bookId, long userId) {
        Optional<Reservation> reservationOpt = reservationRepository.findByBookIdAndUserIdAndStatus(
                bookId, userId, ReservationStatus.READY);

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus(ReservationStatus.COMPLETED);
            final Reservation savedReservation = reservationRepository.saveReservation(reservation);

            return reservationInternalMapper.toDto(savedReservation);
        } else {
            throw new ReservationNotFoundException("Reservation not found");
        }
    }

    /**
     * Counts the number of active reservations for a given book.
     *
     * @param bookId the book to check for active reservations
     * @return the number of active reservations for the book
     */
    public int countActiveReservations(long bookId) {
        return reservationRepository.countByBookIdAndStatusIn(
                bookId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    /**
     * <p>Update queue positions for pending reservations</p>
     *
     * @param bookId book to update queue positions for
     */
    private void updateQueuePositions(long bookId) {
        List<Reservation> pendingReservations = reservationRepository.findByBookIdAndStatusOrderByQueuePosition(
                bookId, ReservationStatus.PENDING);

        int position = 1;
        for (Reservation reservation : pendingReservations) {
            reservation.setQueuePosition(position++);
            reservationRepository.saveReservation(reservation);
        }
    }

    @Override
    public List<ReservationExternalDto> getPendingReservations(long bookId) {
        return reservationRepository
                .findByBookIdAndStatusOrderByQueuePosition(bookId, ReservationStatus.PENDING)
                .stream()
                .map(reservationInternalMapper::toDto)
                .toList();
    }

    @Override
    public void markAsReady(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        reservation.setStatus(ReservationStatus.READY);
        reservationRepository.saveReservation(reservation);
    }

    @Override
    @Transactional
    public void decrementQueuePosition(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        reservation.setQueuePosition(reservation.getQueuePosition() - 1);
        reservation.setUpdatedAt(java.time.Instant.now());

        reservationRepository.saveReservation(reservation);
    }

    @Override
    public boolean isReservedByAnotherUser(long bookId, long userId) {
        return reservationRepository
                .findByBookIdAndStatusOrderByQueuePosition(bookId, ReservationStatus.PENDING)
                .stream()
                .anyMatch(r -> r.getUserId() != userId);
    }

    @Override
    public boolean isBookReservedForAnotherUser(Long bookId, Long currentUserId) {
        return reservationRepository.existsByBookIdAndUserIdNotAndStatusIn(
                bookId, currentUserId, List.of(ReservationStatus.PENDING, ReservationStatus.READY)
        );
    }

    @Override
    @Transactional
    public Reservation expireReservation(long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() != ReservationStatus.READY && reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only active reservations can be expired.");
        }

        reservation.setStatus(ReservationStatus.EXPIRED);
        reservationRepository.saveReservation(reservation);

        updateQueuePositions(reservation.getBookId());

        // Pierwsza rezerwacja READY i została wygaszona, to kolejna staje się READY
        if (reservation.getStatus() == ReservationStatus.READY) {
            Optional<Reservation> next = reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(
                    reservation.getBookId(), ReservationStatus.PENDING);

            next.ifPresent(nextReady -> {
                nextReady.setStatus(ReservationStatus.READY);
                reservationRepository.saveReservation(nextReady);
            });
        }

        return reservation;
    }

    @Override
    @Transactional
    public Reservation completeReservation(long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() != ReservationStatus.READY) {
            throw new IllegalStateException("Reservation is not ready for completion");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.saveReservation(reservation);

        // Pobranie losowego bibliotekarza
        User librarian = userRepository.findRandomLibrarian()
                .orElseThrow(() -> new RuntimeException("No librarian found"));

        Loan loan = new Loan(
                reservation.getBookId(),
                reservation.getUserId(),
                LoanStatus.ACTIVE,
                librarian.getId(),
                null
        );

        loanRepository.saveLoan(loan);

        return reservation;
    }

    @Override
    @Transactional
    public Reservation extendReservation(long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() != ReservationStatus.READY) {
            throw new IllegalStateException("Only READY reservations can be extended.");
        }

        if (reservation.getExpiresAt() == null) {
            throw new IllegalStateException("Cannot extend reservation without expiration date.");
        }

        reservation.setExpiresAt(reservation.getExpiresAt().plus(Duration.ofDays(5)));

        return reservationRepository.saveReservation(reservation);
    }
}
