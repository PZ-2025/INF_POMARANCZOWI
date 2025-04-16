package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    public Optional<Reservation> findFirstByBookAndStatusOrderByQueuePosition(Book book, ReservationStatus status) {
        return reservationJpaRepository.findFirstByBookAndStatusOrderByQueuePosition(book, status);
    }

    public List<Reservation> findByBookAndStatusOrderByQueuePosition(Book book, ReservationStatus status) {
        return reservationJpaRepository.findByBookAndStatusOrderByQueuePosition(book, status);
    }

    public Optional<Reservation> findByBookAndUserAndStatus(Book book, User user, ReservationStatus status) {
        return reservationJpaRepository.findByBookAndUserAndStatus(book, user, status);
    }

    public List<Reservation> findByUser(User user) {
        return reservationJpaRepository.findByUser(user);
    }

    public List<Reservation> findByBook(Book book) {
        return reservationJpaRepository.findByBook(book);
    }

    public List<Reservation> findByBookAndStatusInOrderByQueuePosition(Book book, List<ReservationStatus> statusList) {
        return reservationJpaRepository.findByBookAndStatusInOrderByQueuePosition(book, statusList);
    }

    //findByUserAndStatusInOrderByQueuePosition
    public List<Reservation> findByUserAndStatusInOrderByQueuePosition(User user, List<ReservationStatus> statusList) {
        return reservationJpaRepository.findByUserAndStatusInOrderByQueuePosition(user, statusList);
    }

}
