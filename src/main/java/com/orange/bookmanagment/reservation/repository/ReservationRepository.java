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

    public boolean existsByBookIdAndUserIdAndStatusIn(long bookId, long userId, List<ReservationStatus> statuses) {
        return reservationJpaRepository.existsByBookIdAndUserIdAndStatusIn(bookId, userId, statuses);
    }

    public int countByBookAndStatusIn(Book book, List<ReservationStatus> statusList) {
        return reservationJpaRepository.countByBookIdAndStatusIn(book.getId(), statusList);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    public Optional<Reservation> findFirstByBookAndStatusOrderByQueuePosition(Book book, ReservationStatus status) {
        return reservationJpaRepository.findFirstByBookIdAndStatusOrderByQueuePosition(book.getId(), status);
    }

    public List<Reservation> findByBookAndStatusOrderByQueuePosition(Book book, ReservationStatus status) {
        return reservationJpaRepository.findByBookIdAndStatusOrderByQueuePosition(book.getId(), status);
    }

    public Optional<Reservation> findByBookIdAndUserIdAndStatus(long bookId, long userId, ReservationStatus status) {
        return reservationJpaRepository.findByBookIdAndUserIdAndStatus(bookId, userId, status);
    }

    public List<Reservation> findByUser(User user) {
        return reservationJpaRepository.findByUserId(user.getId());
    }

    public List<Reservation> findByBook(Book book) {
        return reservationJpaRepository.findByBookId(book.getId());
    }

    public List<Reservation> findByBookAndStatusInOrderByQueuePosition(Book book, List<ReservationStatus> statusList) {
        return reservationJpaRepository.findByBookIdAndStatusInOrderByQueuePosition(book.getId(), statusList);
    }

    //findByUserAndStatusInOrderByQueuePosition
    public List<Reservation> findByUserAndStatusInOrderByQueuePosition(User user, List<ReservationStatus> statusList) {
        return reservationJpaRepository.findByUserIdAndStatusInOrderByQueuePosition(user.getId(), statusList);
    }

}
