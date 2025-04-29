package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.shared.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationRepositoryTest {

    @Mock
    private ReservationJpaRepository reservationJpaRepository;

    @InjectMocks
    private ReservationRepository reservationRepository;

    private Reservation reservation;
    private static final long BOOK_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long RESERVATION_ID = 3L;
    private static final List<ReservationStatus> ACTIVE_STATUSES = Arrays.asList(ReservationStatus.PENDING, ReservationStatus.READY);

    @BeforeEach
    void setUp() {
        reservation = new Reservation(BOOK_ID, USER_ID, ReservationStatus.PENDING, 1);
        reservation.setId(RESERVATION_ID);
    }

    @Test
    void saveReservation_shouldDelegateToJpaRepository() {
        // Given
        when(reservationJpaRepository.save(reservation)).thenReturn(reservation);

        // When
        Reservation result = reservationRepository.saveReservation(reservation);

        // Then
        assertEquals(reservation, result);
        verify(reservationJpaRepository).save(reservation);
    }

    @Test
    void existsByBookIdAndUserIdAndStatusIn_shouldDelegateToJpaRepository() {
        // Given
        when(reservationJpaRepository.existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES))
                .thenReturn(true);

        // When
        boolean result = reservationRepository.existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES);

        // Then
        assertTrue(result);
        verify(reservationJpaRepository).existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES);
    }

    @Test
    void countByBookIdAndStatusIn_shouldDelegateToJpaRepository() {
        // Given
        when(reservationJpaRepository.countByBookIdAndStatusIn(BOOK_ID, ACTIVE_STATUSES))
                .thenReturn(5);

        // When
        int result = reservationRepository.countByBookIdAndStatusIn(BOOK_ID, ACTIVE_STATUSES);

        // Then
        assertEquals(5, result);
        verify(reservationJpaRepository).countByBookIdAndStatusIn(BOOK_ID, ACTIVE_STATUSES);
    }

    @Test
    void findById_shouldDelegateToJpaRepository() {
        // Given
        when(reservationJpaRepository.findById(RESERVATION_ID))
                .thenReturn(Optional.of(reservation));

        // When
        Optional<Reservation> result = reservationRepository.findById(RESERVATION_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(reservation, result.get());
        verify(reservationJpaRepository).findById(RESERVATION_ID);
    }

    @Test
    void findFirstByBookIdAndStatusOrderByQueuePosition_shouldDelegateToJpaRepository() {
        // Given
        when(reservationJpaRepository.findFirstByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING))
                .thenReturn(Optional.of(reservation));

        // When
        Optional<Reservation> result = reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(
                BOOK_ID, ReservationStatus.PENDING);

        // Then
        assertTrue(result.isPresent());
        assertEquals(reservation, result.get());
        verify(reservationJpaRepository).findFirstByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING);
    }

    @Test
    void findByBookIdAndStatusOrderByQueuePosition_shouldDelegateToJpaRepository() {
        // Given
        List<Reservation> expected = Collections.singletonList(reservation);
        when(reservationJpaRepository.findByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING))
                .thenReturn(expected);

        // When
        List<Reservation> result = reservationRepository.findByBookIdAndStatusOrderByQueuePosition(
                BOOK_ID, ReservationStatus.PENDING);

        // Then
        assertEquals(expected, result);
        verify(reservationJpaRepository).findByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING);
    }

    @Test
    void findByBookIdAndUserIdAndStatus_shouldDelegateToJpaRepository() {
        // Given
        when(reservationJpaRepository.findByBookIdAndUserIdAndStatus(BOOK_ID, USER_ID, ReservationStatus.READY))
                .thenReturn(Optional.of(reservation));

        // When
        Optional<Reservation> result = reservationRepository.findByBookIdAndUserIdAndStatus(
                BOOK_ID, USER_ID, ReservationStatus.READY);

        // Then
        assertTrue(result.isPresent());
        assertEquals(reservation, result.get());
        verify(reservationJpaRepository).findByBookIdAndUserIdAndStatus(BOOK_ID, USER_ID, ReservationStatus.READY);
    }

    @Test
    void findByUserId_shouldDelegateToJpaRepository() {
        // Given
        List<Reservation> expected = Collections.singletonList(reservation);
        when(reservationJpaRepository.findByUserId(USER_ID))
                .thenReturn(expected);

        // When
        List<Reservation> result = reservationRepository.findByUserId(USER_ID);

        // Then
        assertEquals(expected, result);
        verify(reservationJpaRepository).findByUserId(USER_ID);
    }

    @Test
    void findByBookId_shouldDelegateToJpaRepository() {
        // Given
        List<Reservation> expected = Collections.singletonList(reservation);
        when(reservationJpaRepository.findByBookId(BOOK_ID))
                .thenReturn(expected);

        // When
        List<Reservation> result = reservationRepository.findByBookId(BOOK_ID);

        // Then
        assertEquals(expected, result);
        verify(reservationJpaRepository).findByBookId(BOOK_ID);
    }

    @Test
    void findByBookIdAndStatusInOrderByQueuePosition_shouldDelegateToJpaRepository() {
        // Given
        List<Reservation> expected = Collections.singletonList(reservation);
        when(reservationJpaRepository.findByBookIdAndStatusInOrderByQueuePosition(BOOK_ID, ACTIVE_STATUSES))
                .thenReturn(expected);

        // When
        List<Reservation> result = reservationRepository.findByBookIdAndStatusInOrderByQueuePosition(
                BOOK_ID, ACTIVE_STATUSES);

        // Then
        assertEquals(expected, result);
        verify(reservationJpaRepository).findByBookIdAndStatusInOrderByQueuePosition(BOOK_ID, ACTIVE_STATUSES);
    }

    @Test
    void findByUserIdAndStatusInOrderByQueuePosition_shouldDelegateToJpaRepository() {
        // Given
        List<Reservation> expected = Collections.singletonList(reservation);
        when(reservationJpaRepository.findByUserIdAndStatusInOrderByQueuePosition(USER_ID, ACTIVE_STATUSES))
                .thenReturn(expected);

        // When
        List<Reservation> result = reservationRepository.findByUserIdAndStatusInOrderByQueuePosition(
                USER_ID, ACTIVE_STATUSES);

        // Then
        assertEquals(expected, result);
        verify(reservationJpaRepository).findByUserIdAndStatusInOrderByQueuePosition(USER_ID, ACTIVE_STATUSES);
    }
}