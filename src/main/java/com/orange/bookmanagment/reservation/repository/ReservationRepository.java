package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.shared.enums.ReservationStatus;
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

    public int countByBookIdAndStatusIn(long bookId, List<ReservationStatus> statusList) {
        return reservationJpaRepository.countByBookIdAndStatusIn(bookId, statusList);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    public Optional<Reservation> findFirstByBookIdAndStatusOrderByQueuePosition(long bookId, ReservationStatus status) {
        return reservationJpaRepository.findFirstByBookIdAndStatusOrderByQueuePosition(bookId, status);
    }

    public List<Reservation> findByBookIdAndStatusOrderByQueuePosition(long bookId, ReservationStatus status) {
        return reservationJpaRepository.findByBookIdAndStatusOrderByQueuePosition(bookId, status);
    }

    public Optional<Reservation> findByBookIdAndUserIdAndStatus(long bookId, long userId, ReservationStatus status) {
        return reservationJpaRepository.findByBookIdAndUserIdAndStatus(bookId, userId, status);
    }

    public List<Reservation> findByUserId(long userId) {
        return reservationJpaRepository.findByUserId(userId);
    }

    public List<Reservation> findByBookId(long bookId) {
        return reservationJpaRepository.findByBookId(bookId);
    }

    public List<Reservation> findByBookIdAndStatusInOrderByQueuePosition(long bookId, List<ReservationStatus> statusList) {
        return reservationJpaRepository.findByBookIdAndStatusInOrderByQueuePosition(bookId, statusList);
    }

    public List<Reservation> findByUserIdAndStatusInOrderByQueuePosition(Long userId, List<ReservationStatus> statusList) {
        return reservationJpaRepository.findByUserIdAndStatusInOrderByQueuePosition(userId, statusList);
    }

    public boolean existsByBookIdAndUserIdNotAndStatusIn(long bookId, long userId, List<ReservationStatus> statuses) {
        return reservationJpaRepository.existsByBookIdAndUserIdNotAndStatusIn(bookId, userId, statuses);
    }
}
