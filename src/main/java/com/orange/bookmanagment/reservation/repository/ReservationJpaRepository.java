package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation,Long> {

    boolean existsByBookAndUserAndStatusIn(Book book, User user, List<ReservationStatus> statusList);

    int countByBookAndStatusIn(Book book, List<ReservationStatus> statusList);

}
