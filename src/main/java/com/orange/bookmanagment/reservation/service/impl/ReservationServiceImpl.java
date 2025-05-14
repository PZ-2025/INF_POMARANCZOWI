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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Implementacja serwisu {@link ReservationService} oraz {@link ReservationExternalService}.
 * <p>
 * Obsługuje logikę tworzenia, anulowania, aktualizacji i finalizacji rezerwacji książek w systemie.
 */
@Service
@RequiredArgsConstructor
class ReservationServiceImpl implements ReservationService, ReservationExternalService {

    private final ReservationRepository reservationRepository;
    private final BookExternalService bookExternalService;
    private final ReservationInternalMapper reservationInternalMapper;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    /**
     * Tworzy nową rezerwację książki przez użytkownika.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @return utworzona rezerwacja
     * @throws BookNotAvailableException jeśli książka jest niedostępna
     * @throws BookAlreadyReservedException jeśli użytkownik ma już aktywną rezerwację tej książki
     */
    @Override
    @Transactional
    public Reservation createReservation(long bookId, long userId) {
        BookStatus bookStatus = bookExternalService.getBookStatusForExternal(bookId);

        if (bookStatus == BookStatus.LOST) {
            throw new BookNotAvailableException("This book is currently reported as lost and cannot be reserved");
        }

        if (isBookReservedForUser(bookId, userId)) {
            throw new BookAlreadyReservedException("You already have an active reservation for this book");
        }

        int queuePosition = countActiveReservations(bookId) + 1;
        ReservationStatus status = ReservationStatus.PENDING;

        if (bookStatus == BookStatus.AVAILABLE && queuePosition == 1) {
            status = ReservationStatus.READY;
            bookExternalService.updateBookStatus(bookId, BookStatus.BORROWED);
        } else if (bookStatus == BookStatus.BORROWED && queuePosition == 1) {
            bookExternalService.updateBookStatus(bookId, BookStatus.RESERVED);
        }

        Reservation reservation = new Reservation(
                bookId,
                userId,
                status,
                queuePosition
        );

        return reservationRepository.saveReservation(reservation);
    }

    /**
     * Anuluje aktywną rezerwację użytkownika.
     *
     * @param reservation rezerwacja do anulowania
     * @return zaktualizowana rezerwacja
     */
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
                bookExternalService.updateBookStatus(bookId, BookStatus.RESERVED);
            } else {
                // Brak innych rezerwacji, oznacz książkę jako dostępną
                bookExternalService.updateBookStatus(bookId, BookStatus.AVAILABLE);
            }
        } else {
            // Anulowana rezerwacja była PENDING i nie ma żadnej READY to sprawdzenie, czy coś zostało
            boolean hasActive = reservationRepository.existsByBookIdAndStatusIn(
                    bookId, List.of(ReservationStatus.PENDING, ReservationStatus.READY)
            );
            if (!hasActive) {
                bookExternalService.updateBookStatus(bookId, BookStatus.AVAILABLE);
            }
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.saveReservation(reservation);
        updateQueuePositions(bookId);

        updateBookStatusBasedOnReservationsAndLoans(bookId);

        return reservation;
    }

    /**
     * Przetwarza zwróconą książkę i przypisuje ją do następnej osoby w kolejce (jeśli istnieje).
     *
     * @param bookId ID zwróconej książki
     * @return true, jeśli zaktualizowano status rezerwacji; false w przeciwnym razie
     */
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
            updateBookStatusBasedOnReservationsAndLoans(bookId);

            // Zaktualizuj pozycje w kolejce
            updateQueuePositions(bookId);

            return true;
        }

        return false;
    }

    /**
     * Zwraca rezerwację po ID.
     *
     * @param reservationId ID rezerwacji
     * @return rezerwacja
     * @throws ReservationNotFoundException jeśli nie znaleziono rezerwacji
     */
    @Override
    @Transactional
    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
    }

    /**
     * Pobiera wszystkie rezerwacje użytkownika, aktualizując status wygasłych.
     *
     * @param userId ID użytkownika
     * @return lista rezerwacji
     */
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

    /**
     * Zwraca wszystkie aktywne rezerwacje użytkownika.
     *
     * @param userId ID użytkownika
     * @return lista aktywnych rezerwacji użytkownika
     */
    @Override
    public List<Reservation> getActiveUserReservations(long userId) {
        return reservationRepository.findByUserIdAndStatusInOrderByQueuePosition(
                userId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    /**
     * Zwraca wszystkie rezerwacje dla danej książki (niezależnie od statusu).
     *
     * @param bookId ID książki
     * @return lista rezerwacji dla książki
     */
    @Override
    public List<Reservation> getBookReservations(long bookId) {
        return reservationRepository.findByBookId(bookId);
    }

    /**
     * Zwraca aktywne rezerwacje danej książki (statusy PENDING i READY), posortowane po kolejce.
     *
     * @param bookId ID książki
     * @return lista aktywnych rezerwacji dla książki
     */
    @Override
    public List<Reservation> getActiveBookReservations(long bookId) {
        return reservationRepository.findByBookIdAndStatusInOrderByQueuePosition(
                bookId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    /**
     * Sprawdza, czy dana książka jest zarezerwowana przez użytkownika.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @return true, jeśli rezerwacja istnieje
     */
    public boolean isBookReservedForUser(Long bookId, Long userId) {
        return reservationRepository.existsByBookIdAndUserIdAndStatusIn(
                bookId, userId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    /**
     * Zlicza aktywne rezerwacje na daną książkę.
     *
     * @param bookId ID książki
     * @return liczba rezerwacji
     */
    public int countActiveReservations(long bookId) {
        return reservationRepository.countByBookIdAndStatusIn(
                bookId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    /**
     * Aktualizuje pozycje w kolejce dla oczekujących rezerwacji.
     *
     * @param bookId ID książki
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

    /**
     * Zwraca listę oczekujących rezerwacji na daną książkę, posortowanych według pozycji w kolejce.
     *
     * @param bookId ID książki
     * @return lista rezerwacji w statusie PENDING
     */
    @Override
    public List<ReservationExternalDto> getPendingReservations(long bookId) {
        return reservationRepository
                .findByBookIdAndStatusOrderByQueuePosition(bookId, ReservationStatus.PENDING)
                .stream()
                .map(reservationInternalMapper::toDto)
                .toList();
    }

    /**
     * Ustawia rezerwację jako gotową do odbioru.
     *
     * @param reservationId ID rezerwacji
     * @throws ReservationNotFoundException jeśli rezerwacja nie istnieje
     */
    @Override
    public void markAsReady(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        reservation.setStatus(ReservationStatus.READY);
        reservationRepository.saveReservation(reservation);
    }

    /**
     * Dekrementuje pozycję w kolejce rezerwacji (zmniejsza o 1).
     *
     * @param reservationId ID rezerwacji
     * @throws ReservationNotFoundException jeśli rezerwacja nie istnieje
     */
    @Override
    @Transactional
    public void decrementQueuePosition(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        reservation.setQueuePosition(reservation.getQueuePosition() - 1);
        reservation.setUpdatedAt(java.time.Instant.now());

        reservationRepository.saveReservation(reservation);
    }

    /**
     * Sprawdza, czy książka jest zarezerwowana przez innego użytkownika niż wskazany.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @return true, jeśli inny użytkownik posiada aktywną rezerwację; false w przeciwnym razie
     */
    @Override
    public boolean isReservedByAnotherUser(long bookId, long userId) {
        return reservationRepository
                .findByBookIdAndStatusOrderByQueuePosition(bookId, ReservationStatus.PENDING)
                .stream()
                .anyMatch(r -> r.getUserId() != userId);
    }

    /**
     * Sprawdza, czy książka jest zarezerwowana przez kogoś innego niż obecny użytkownik.
     *
     * @param bookId ID książki
     * @param currentUserId ID obecnie zalogowanego użytkownika
     * @return true, jeśli ktoś inny ma aktywną rezerwację; false w przeciwnym razie
     */
    @Override
    public boolean isBookReservedForAnotherUser(Long bookId, Long currentUserId) {
        return reservationRepository.existsByBookIdAndUserIdNotAndStatusIn(
                bookId, currentUserId, List.of(ReservationStatus.PENDING, ReservationStatus.READY)
        );
    }

    /**
     * Wygasza aktywną rezerwację. Jeśli była READY — przekazuje do kolejnego.
     *
     * @param reservationId ID rezerwacji
     * @return zaktualizowana rezerwacja
     */
    @Override
    @Transactional
    public Reservation expireReservation(long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() != ReservationStatus.READY && reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only active reservations can be expired.");
        }

        boolean wasReady = reservation.getStatus() == ReservationStatus.READY;
        reservation.setStatus(ReservationStatus.EXPIRED);
        reservationRepository.saveReservation(reservation);

        long bookId = reservation.getBookId();
        updateQueuePositions(bookId);

        // Pierwsza rezerwacja READY i została wygaszona, to kolejna staje się READY
        if (wasReady) {
            Optional<Reservation> next = reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(
                    reservation.getBookId(), ReservationStatus.PENDING);

            if (next.isPresent()) {
                Reservation nextReady = next.get();
                nextReady.setStatus(ReservationStatus.READY);
                reservationRepository.saveReservation(nextReady);
                bookExternalService.updateBookStatus(bookId, BookStatus.RESERVED);
            } else {
                bookExternalService.updateBookStatus(bookId, BookStatus.AVAILABLE);
            }
        } else {
            updateBookStatusBasedOnReservationsAndLoans(bookId);
        }

        return reservation;
    }

    /**
     * Finalizuje rezerwację oraz tworzy nowe wypożyczenie.
     *
     * @param reservationId ID rezerwacji
     * @return zaktualizowana rezerwacja
     */
    @Override
    @Transactional
    public Reservation completeReservation(long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() != ReservationStatus.READY) {
            throw new IllegalStateException("Reservation is not ready for completion");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.saveReservation(reservation);

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

        updateBookStatusBasedOnReservationsAndLoans(reservation.getBookId());

        return reservation;
    }

    /**
     * Finalizuje rezerwację użytkownika dla danej książki.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @return DTO rezerwacji
     */
    @Override
    @Transactional
    public ReservationExternalDto completeReservation(long bookId, long userId) {
        Optional<Reservation> reservationOpt = reservationRepository.findByBookIdAndUserIdAndStatus(
                bookId, userId, ReservationStatus.READY);

        if (reservationOpt.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found");
        }

        Reservation reservation = reservationOpt.get();
        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation savedReservation = reservationRepository.saveReservation(reservation);

        updateBookStatusBasedOnReservationsAndLoans(bookId);

        return reservationInternalMapper.toDto(savedReservation);
    }

    /**
     * Przedłuża rezerwację o 5 dni.
     *
     * @param reservationId ID rezerwacji
     * @return zaktualizowana rezerwacja
     */
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

    /**
     * Aktualizuje status książki w systemie zewnętrznym na podstawie jej bieżących rezerwacji i wypożyczeń.
     * <p>
     * Logika aktualizacji:
     * <ul>
     *   <li>Jeśli istnieją aktywne rezerwacje (status {@code PENDING} lub {@code READY}) – ustaw status na {@code RESERVED}.</li>
     *   <li>W przeciwnym razie, jeśli istnieją aktywne wypożyczenia (status {@code ACTIVE} lub {@code OVERDUE}) – ustaw status na {@code BORROWED}.</li>
     *   <li>Jeśli brak zarówno aktywnych rezerwacji, jak i wypożyczeń – ustaw status na {@code AVAILABLE}.</li>
     * </ul>
     *
     * @param bookId ID książki, dla której należy zaktualizować status
     */
    private void updateBookStatusBasedOnReservationsAndLoans(long bookId) {
        boolean hasActiveReservations = reservationRepository.existsByBookIdAndStatusIn(
                bookId, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
        boolean hasActiveLoans = loanRepository.existsByBookIdAndStatusIn(
                bookId, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));

        if (hasActiveReservations) {
            bookExternalService.updateBookStatus(bookId, BookStatus.RESERVED);
        } else if (hasActiveLoans) {
            bookExternalService.updateBookStatus(bookId, BookStatus.BORROWED);
        } else {
            bookExternalService.updateBookStatus(bookId, BookStatus.AVAILABLE);
        }
    }

    /**
     * Zwraca aktywne rezerwacje (PENDING lub READY) książki na potrzeby oznaczenia jako zgubiona.
     *
     * @param bookId ID książki
     * @return lista aktywnych rezerwacji
     */
    @Override
    public List<ReservationExternalDto> getActiveBookReservationsForMark(long bookId) {
        return reservationRepository
                .findByBookIdAndStatusInOrderByQueuePosition(bookId, List.of(ReservationStatus.PENDING, ReservationStatus.READY))
                .stream()
                .map(reservationInternalMapper::toDto)
                .toList();
    }

    /**
     * Anuluje rezerwację na potrzeby oznaczenia książki jako zgubionej.
     *
     * @param reservationId ID rezerwacji
     */
    @Override
    @Transactional
    public void cancelReservationForMark(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.saveReservation(reservation);

        updateQueuePositions(reservation.getBookId());
    }
}
