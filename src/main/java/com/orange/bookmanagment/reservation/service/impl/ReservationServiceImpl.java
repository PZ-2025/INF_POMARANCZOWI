package com.orange.bookmanagment.reservation.service.impl;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.reservation.repository.ReservationRepository;
import com.orange.bookmanagment.reservation.service.ReservationService;
import com.orange.bookmanagment.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
//    private final LoanService loanService;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Reservation createReservation(Book book, User user) {

        // Sprawdz czy książka nie jest już wypożyczona przez tego użytkownika
//        if (loanService.isBookBorrowed(book) && loanService.getActiveUserLoans(user).stream()
//                .anyMatch(loan -> loan.getBook().equals(book))) {
//            throw new IllegalStateException("You already have this book borrowed");
//        }

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
                book,
                user,
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

    public boolean isBookReservedForUser(Book book, User user) {
        return reservationRepository.existsByBookAndUserAndStatusIn(
                book, user, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    public int countActiveReservations(Book book) {
        return reservationRepository.countByBookAndStatusIn(
                book, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }
}
