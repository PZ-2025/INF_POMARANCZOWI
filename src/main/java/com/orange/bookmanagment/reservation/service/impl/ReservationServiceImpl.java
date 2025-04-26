package com.orange.bookmanagment.reservation.service.impl;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.exception.BookNotAvailableException;
import com.orange.bookmanagment.reservation.exception.ReservationNotFoundException;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.reservation.repository.ReservationRepository;
import com.orange.bookmanagment.reservation.service.ReservationService;
import com.orange.bookmanagment.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ReservationServiceImpl is an implementation of the ReservationService interface.
 * It provides methods to create reservations and check the status of reservations.
 */
@Service
@RequiredArgsConstructor
class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    //private final LoanService loanService;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Reservation createReservation(Book book, User user) {

        if (book.getStatus() == BookStatus.BORROWED) {
            // Sprawdź czy to użytkownik wypożyczył tę książkę
            //if (loanService.isBookBorrowedByUser(book, user)) {
            //    throw new IllegalStateException("You already have this book borrowed");
           // }
            // Książka jest wypożyczona przez kogoś innego - można kontynuować rezerwację
        } else if (book.getStatus() == BookStatus.LOST) {
            throw new BookNotAvailableException("This book is currently reported as lost and cannot be reserved");
        }

        // Sprawdz czy użytkownik nie ma już aktywnej rezerwacji na tę książkę
        if (isBookReservedForUser(book, user)) {
            throw new BookAlreadyReservedException("You already have an active reservation for this book");
        }

        // Sprawdz czy użytkownik nie przekroczył limitu rezerwacji
//        List<Reservation> activeReservations = getActiveReservations(user);
//        if (activeReservations.size() >= maxReservationsPerUser) {
//            throw new IllegalStateException("You have reached the maximum number of active reservations ("
//                    + maxReservationsPerUser + ")");
//        }

        final int queuePosition = countActiveReservations(book) + 1;

        Reservation reservation = new Reservation(
                book.getId(),
                user.getId(),
                ReservationStatus.PENDING,
                queuePosition
        );

        if (book.getStatus() == BookStatus.AVAILABLE) {
            reservation.setStatus(ReservationStatus.READY);

            book.setStatus(BookStatus.RESERVED);
            bookRepository.saveBook(book);
        }

        return reservationRepository.saveReservation(reservation);
    }

    @Override
    @Transactional
    public Reservation cancelReservation(Reservation reservation) {

        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.READY) {
            throw new IllegalStateException("Cannot cancel a reservation that is not active");
        }

        // todo: ogarnąć to bez book
//        if (reservation.getStatus() == ReservationStatus.READY) {
//            Book book = reservation.getBook();
//
//            Optional<Reservation> nextReservation = reservationRepository.findFirstByBookAndStatusOrderByQueuePosition(
//                    book, ReservationStatus.PENDING);
//
//            if (nextReservation.isPresent()) {
//                // Oznacz następną rezerwację jako gotową do odbioru
//                nextReservation.get().setStatus(ReservationStatus.READY);
//                reservationRepository.saveReservation(nextReservation.get());
//            } else {
//                // Brak innych rezerwacji, oznacz książkę jako dostępną
//                book.setStatus(BookStatus.AVAILABLE);
//                bookRepository.saveBook(book);
//            }
//        }

        reservation.setStatus(ReservationStatus.CANCELLED);


        // Aktualizuj pozycje w kolejce pozostałych rezerwacji
//        updateQueuePositions(reservation.getBookId()); //todo: ogarnąć to bez book

        return reservationRepository.saveReservation(reservation);

    }

    @Override
    @Transactional
    public boolean completeReservation(long bookId, long userId) {
        Optional<Reservation> reservationOpt = reservationRepository.findByBookIdAndUserIdAndStatus(
                bookId, userId, ReservationStatus.READY);

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus(ReservationStatus.COMPLETED);
            reservationRepository.saveReservation(reservation);
            return true;
        }

        return false;
    }

    //todo: ogarnąć to bez book
//    @Override
//    @Transactional
//    public boolean processReturnedBook(long bookId) {
//        Optional<Reservation> nextReservation = reservationRepository.findFirstByBookAndStatusOrderByQueuePosition(
//                book, ReservationStatus.PENDING);
//
//        if (nextReservation.isPresent()) {
//            Reservation reservation = nextReservation.get();
//
//            // Oznacz rezerwację jako gotową do odbioru
//            reservation.setStatus(ReservationStatus.READY);
//            reservationRepository.saveReservation(reservation);
//
//            // Zaktualizuj status książki
//            book.setStatus(BookStatus.RESERVED);
//            bookRepository.saveBook(book);
//
//            // Zaktualizuj pozycje w kolejce
//            updateQueuePositions(book);
//
//            return true;
//        }
//
//        return false;
//    }

    @Override
    @Transactional
    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
    }

    @Override
    @Transactional
    public List<Reservation> getUserReservations(User user) {
        return reservationRepository.findByUser(user);
    }

    @Override
    public List<Reservation> getActiveUserReservations(User user) {
        return reservationRepository.findByUserAndStatusInOrderByQueuePosition(
                user, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    @Override
    public List<Reservation> getBookReservations(Book book) {
        return reservationRepository.findByBook(book);
    }

    @Override
    public List<Reservation> getActiveBookReservations(Book book) {
        return reservationRepository.findByBookAndStatusInOrderByQueuePosition(
                book, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
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

    /**
     * Counts the number of active reservations for a given book.
     *
     * @param book the book to check for active reservations
     * @return the number of active reservations for the book
     */
    public int countActiveReservations(Book book) {
        return reservationRepository.countByBookAndStatusIn(
                book, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    /**
     * <p>Update queue positions for pending reservations</p>
     *
     * @param book book to update queue positions for
     */
    private void updateQueuePositions(Book book) {
        List<Reservation> pendingReservations = reservationRepository.findByBookAndStatusOrderByQueuePosition(
                book, ReservationStatus.PENDING);

        int position = 1;
        for (Reservation reservation : pendingReservations) {
            reservation.setQueuePosition(position++);
            reservationRepository.saveReservation(reservation);
        }
    }



}
