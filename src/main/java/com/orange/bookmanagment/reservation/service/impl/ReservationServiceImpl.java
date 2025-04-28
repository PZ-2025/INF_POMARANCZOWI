package com.orange.bookmanagment.reservation.service.impl;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.reservation.api.ReservationExternalService;
import com.orange.bookmanagment.reservation.service.mapper.ReservationInternalMapper;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.exception.BookNotAvailableException;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.shared.enums.ReservationStatus;
import com.orange.bookmanagment.reservation.repository.ReservationRepository;
import com.orange.bookmanagment.reservation.service.ReservationService;
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
class ReservationServiceImpl implements ReservationService, ReservationExternalService {

    private final ReservationRepository reservationRepository;
    private final BookExternalService bookExternalService;
    private final ReservationInternalMapper reservationInternalMapper;

//    @Override
//    @Transactional
//    public Reservation createReservation(Book book, User user) {
//
//        if (book.getStatus() == BookStatus.BORROWED) {
//            // Sprawdź czy to użytkownik wypożyczył tę książkę
//            //if (loanService.isBookBorrowedByUser(book, user)) {
//            //    throw new IllegalStateException("You already have this book borrowed");
//           // }
//            // Książka jest wypożyczona przez kogoś innego - można kontynuować rezerwację
//        } else if (book.getStatus() == BookStatus.LOST) {
//            throw new BookNotAvailableException("This book is currently reported as lost and cannot be reserved");
//        }
//
//        // Sprawdz czy użytkownik nie ma już aktywnej rezerwacji na tę książkę
//        if (isBookReservedForUser(book, user)) {
//            throw new BookAlreadyReservedException("You already have an active reservation for this book");
//        }
//
//        // Sprawdz czy użytkownik nie przekroczył limitu rezerwacji
////        List<Reservation> activeReservations = getActiveReservations(user);
////        if (activeReservations.size() >= maxReservationsPerUser) {
////            throw new IllegalStateException("You have reached the maximum number of active reservations ("
////                    + maxReservationsPerUser + ")");
////        }
//
//        final int queuePosition = countActiveReservations(book) + 1;
//
//        Reservation reservation = new Reservation(
//                book.getId(),
//                user.getId(),
//                ReservationStatus.PENDING,
//                queuePosition
//        );
//
//        if (book.getStatus() == BookStatus.AVAILABLE) {
//            reservation.setStatus(ReservationStatus.READY);
//
//            book.setStatus(BookStatus.RESERVED);
//            bookExternalService.saveBook(book);
//        }
//
//        return reservationRepository.saveReservation(reservation);
//    }

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

//    // Implementacje metod interfejsu zewnętrznego
//    @Override
//    public ReservationInternalDto createReservation(long bookId, long userId) {
//        Reservation reservation = this.createReservation(bookId, userId);
//        return reservationInternalMapper.toDto(reservation);
//    }
//
//    @Override
//    @Transactional
//    public Reservation cancelReservation(Reservation reservation) {
//
//        if (reservation.getStatus() != ReservationStatus.PENDING
//                && reservation.getStatus() != ReservationStatus.READY) {
//            throw new IllegalStateException("Cannot cancel a reservation that is not active");
//        }
//
//        // todo: ogarnąć to bez book
////        if (reservation.getStatus() == ReservationStatus.READY) {
////            Book book = reservation.getBook();
////
////            Optional<Reservation> nextReservation = reservationRepository.findFirstByBookAndStatusOrderByQueuePosition(
////                    book, ReservationStatus.PENDING);
////
////            if (nextReservation.isPresent()) {
////                // Oznacz następną rezerwację jako gotową do odbioru
////                nextReservation.get().setStatus(ReservationStatus.READY);
////                reservationRepository.saveReservation(nextReservation.get());
////            } else {
////                // Brak innych rezerwacji, oznacz książkę jako dostępną
////                book.setStatus(BookStatus.AVAILABLE);
////                bookRepository.saveBook(book);
////            }
////        }
//
//        reservation.setStatus(ReservationStatus.CANCELLED);
//
//
//        // Aktualizuj pozycje w kolejce pozostałych rezerwacji
////        updateQueuePositions(reservation.getBookId()); //todo: ogarnąć to bez book
//
//        return reservationRepository.saveReservation(reservation);
//
//    }
//
//    @Override
//    public ReservationInternalDto createReservationExternal(long bookId, long userId) {
//        Reservation reservation = this.createReservation(bookId, userId);
//        return reservationMapper.toDto(reservation);
//    }
//
    @Override
    @Transactional
    public void completeReservation(long bookId, long userId) {
        Optional<Reservation> reservationOpt = reservationRepository.findByBookIdAndUserIdAndStatus(
                bookId, userId, ReservationStatus.READY);

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus(ReservationStatus.COMPLETED);
            reservationRepository.saveReservation(reservation);
        }

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

//    @Override
//    @Transactional
//    public Reservation getReservationById(Long reservationId) {
//        return reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
//    }
//
//    @Override
//    @Transactional
//    public List<Reservation> getUserReservations(User user) {
//        return reservationRepository.findByUser(user);
//    }
//
//    @Override
//    public List<Reservation> getActiveUserReservations(User user) {
//        return reservationRepository.findByUserAndStatusInOrderByQueuePosition(
//                user, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
//    }
//
//    @Override
//    public List<Reservation> getBookReservations(Book book) {
//        return reservationRepository.findByBook(book);
//    }
//
//    @Override
//    public List<Reservation> getActiveBookReservations(Book book) {
//        return reservationRepository.findByBookAndStatusInOrderByQueuePosition(
//                book, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
//    }

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



}
