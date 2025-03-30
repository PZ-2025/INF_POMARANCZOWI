package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    public Reservation saveReservation(Reservation reservation){
        return reservationJpaRepository.save(reservation);
    }

    public boolean existsByBookAndUserAndStatusIn(Book book, User user, List<ReservationStatus> statusList) {
        return reservationJpaRepository.existsByBookAndUserAndStatusIn(book, user, statusList);
    }

    public int countByBookAndStatusIn(Book book, List<ReservationStatus> statusList) {
        return reservationJpaRepository.countByBookAndStatusIn(book, statusList);
    }


}
