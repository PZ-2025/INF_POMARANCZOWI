package com.orange.bookmanagment.reservation.service.impl;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.reservation.api.dto.ReservationExternalDto;
import java.time.Instant;
import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.exception.BookNotAvailableException;
import com.orange.bookmanagment.reservation.exception.ReservationNotFoundException;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.repository.ReservationRepository;
import com.orange.bookmanagment.reservation.service.mapper.ReservationInternalMapper;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.bookmanagment.shared.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookExternalService bookExternalService;

    @Mock
    private ReservationInternalMapper reservationInternalMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private static final long BOOK_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long RESERVATION_ID = 3L;

    private Reservation reservation;
    private ReservationExternalDto reservationExternalDto;

    @BeforeEach
    void setUp() {
        reservation = new Reservation(BOOK_ID, USER_ID, ReservationStatus.PENDING, 1);
        reservation.setId(RESERVATION_ID);

        Instant now = Instant.now();
        reservationExternalDto = new ReservationExternalDto(
                RESERVATION_ID,
                BOOK_ID,
                USER_ID,
                ReservationStatus.PENDING,
                1,
                now,
                now.plusSeconds(60 * 60 * 24), // expires in 24 hours
                now
        );
    }

    // Tests for createReservation method

    @Test
    void createReservation_whenBookIsAvailable_thenReservationIsReadyAndBookIsReserved() {
        // Given
        when(bookExternalService.getBookStatusForExternal(BOOK_ID)).thenReturn(BookStatus.AVAILABLE);
        when(reservationRepository.saveReservation(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Reservation result = reservationService.createReservation(BOOK_ID, USER_ID);

        // Then
        assertEquals(ReservationStatus.READY, result.getStatus());
        assertEquals(BOOK_ID, result.getBookId());
        assertEquals(USER_ID, result.getUserId());
        verify(bookExternalService).updateBookStatus(BOOK_ID, BookStatus.RESERVED);
    }

    @Test
    void createReservation_whenBookIsReserved_thenReservationIsPendingAndQueuePositionIsSet() {
        // Given
        when(bookExternalService.getBookStatusForExternal(BOOK_ID)).thenReturn(BookStatus.RESERVED);
        when(reservationRepository.countByBookIdAndStatusIn(eq(BOOK_ID), anyList())).thenReturn(2);
        when(reservationRepository.saveReservation(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Reservation result = reservationService.createReservation(BOOK_ID, USER_ID);

        // Then
        assertEquals(ReservationStatus.PENDING, result.getStatus());
        assertEquals(3, result.getQueuePosition()); // 2 existing + 1
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
    }

    @Test
    void createReservation_whenBookIsBorrowed_thenThrowsBookNotAvailableException() {
        // Given
        when(bookExternalService.getBookStatusForExternal(BOOK_ID)).thenReturn(BookStatus.BORROWED);

        // When & Then
        assertThrows(BookNotAvailableException.class, () -> reservationService.createReservation(BOOK_ID, USER_ID));
        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
    }

    @Test
    void createReservation_whenBookIsLost_thenThrowsBookNotAvailableException() {
        // Given
        when(bookExternalService.getBookStatusForExternal(BOOK_ID)).thenReturn(BookStatus.LOST);

        // When & Then
        assertThrows(BookNotAvailableException.class, () -> reservationService.createReservation(BOOK_ID, USER_ID));
        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
    }

    @Test
    void createReservation_whenBookAlreadyReservedByUser_thenThrowsBookAlreadyReservedException() {
        // Given
        when(bookExternalService.getBookStatusForExternal(BOOK_ID)).thenReturn(BookStatus.RESERVED);
        when(reservationRepository.existsByBookIdAndUserIdAndStatusIn(eq(BOOK_ID), eq(USER_ID), anyList())).thenReturn(true);

        // When & Then
        assertThrows(BookAlreadyReservedException.class, () -> reservationService.createReservation(BOOK_ID, USER_ID));
        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
    }

    // Tests for cancelReservation method

    @Test
    void cancelReservation_whenStatusIsReadyAndThereIsNextReservation_thenNextReservationBecomesReady() {
        // Given
        Reservation reservationToCancel = new Reservation(BOOK_ID, USER_ID, ReservationStatus.READY, 1);
        Reservation nextReservation = new Reservation(BOOK_ID, 3L, ReservationStatus.PENDING, 2);

        when(reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING))
                .thenReturn(Optional.of(nextReservation));
        when(reservationRepository.saveReservation(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Reservation result = reservationService.cancelReservation(reservationToCancel);

        // Then
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        assertEquals(ReservationStatus.READY, nextReservation.getStatus());
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(reservationRepository).saveReservation(nextReservation);
    }

    @Test
    void cancelReservation_whenStatusIsReadyAndNoNextReservation_thenBookBecomesAvailable() {
        // Given
        Reservation reservationToCancel = new Reservation(BOOK_ID, USER_ID, ReservationStatus.READY, 1);

        when(reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING))
                .thenReturn(Optional.empty());
        when(reservationRepository.saveReservation(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Reservation result = reservationService.cancelReservation(reservationToCancel);

        // Then
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        verify(bookExternalService).updateBookStatus(BOOK_ID, BookStatus.AVAILABLE);
    }

    @Test
    void cancelReservation_whenStatusIsPending_thenStatusBecomesCancel() {
        // Given
        Reservation reservationToCancel = new Reservation(BOOK_ID, USER_ID, ReservationStatus.PENDING, 2);

        when(reservationRepository.saveReservation(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Reservation result = reservationService.cancelReservation(reservationToCancel);

        // Then
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
    }

    @Test
    void cancelReservation_whenStatusIsAlreadyCancelled_thenNoChangeIsNeeded() {
        // Given
        Reservation reservationToCancel = new Reservation(BOOK_ID, USER_ID, ReservationStatus.CANCELLED, 1);

        // When
        Reservation result = reservationService.cancelReservation(reservationToCancel);

        // Then
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
    }

    @Test
    void cancelReservation_whenStatusIsCompleted_thenThrowsIllegalStateException() {
        // Given
        Reservation reservationToCancel = new Reservation(BOOK_ID, USER_ID, ReservationStatus.COMPLETED, 1);

        // When & Then
        assertThrows(IllegalStateException.class, () -> reservationService.cancelReservation(reservationToCancel));
        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
    }

    // Tests for completeReservation method

    @Test
    void completeReservation_whenReservationIsReady_thenStatusBecomesCompleted() {
        // Given
        Reservation readyReservation = new Reservation(BOOK_ID, USER_ID, ReservationStatus.READY, 1);

        when(reservationRepository.findByBookIdAndUserIdAndStatus(BOOK_ID, USER_ID, ReservationStatus.READY))
                .thenReturn(Optional.of(readyReservation));
        when(reservationRepository.saveReservation(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation saved = invocation.getArgument(0);
            assertEquals(ReservationStatus.COMPLETED, saved.getStatus());
            return saved;
        });

        // Utwórz DTO z odpowiednim statusem COMPLETED
        Instant now = Instant.now();
        ReservationExternalDto completedDto = new ReservationExternalDto(
                RESERVATION_ID,
                BOOK_ID,
                USER_ID,
                ReservationStatus.COMPLETED,
                1,
                now,
                now.plusSeconds(60 * 60 * 24),
                now
        );

        when(reservationInternalMapper.toDto(any(Reservation.class))).thenReturn(completedDto);

        // When
        ReservationExternalDto result = reservationService.completeReservation(BOOK_ID, USER_ID);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.COMPLETED, result.status());
        verify(reservationRepository).saveReservation(readyReservation);
        verify(reservationInternalMapper).toDto(readyReservation);
    }

    @Test
    void completeReservation_whenReservationNotFound_thenThrowsReservationNotFoundException() {
        // Given
        when(reservationRepository.findByBookIdAndUserIdAndStatus(BOOK_ID, USER_ID, ReservationStatus.READY))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ReservationNotFoundException.class, () -> reservationService.completeReservation(BOOK_ID, USER_ID));
        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
    }

    // Tests for processReturnedBook method

    @Test
    void processReturnedBook_whenPendingReservationsExist_thenFirstReservationBecomesReady() {
        // Given
        Reservation pendingReservation = new Reservation(BOOK_ID, USER_ID, ReservationStatus.PENDING, 1);

        when(reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING))
                .thenReturn(Optional.of(pendingReservation));
        when(reservationRepository.saveReservation(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = reservationService.processReturnedBook(BOOK_ID);

        // Then
        assertTrue(result);
        assertEquals(ReservationStatus.READY, pendingReservation.getStatus());
        verify(bookExternalService).updateBookStatus(BOOK_ID, BookStatus.RESERVED);
    }

    @Test
    void processReturnedBook_whenNoPendingReservations_thenReturnFalse() {
        // Given
        when(reservationRepository.findFirstByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING))
                .thenReturn(Optional.empty());

        // When
        boolean result = reservationService.processReturnedBook(BOOK_ID);

        // Then
        assertFalse(result);
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
    }

    // Tests for getReservationById method

    @Test
    void getReservationById_whenReservationExists_thenReturnReservation() {
        // Given
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // When
        Reservation result = reservationService.getReservationById(RESERVATION_ID);

        // Then
        assertEquals(reservation, result);
    }

    @Test
    void getReservationById_whenReservationDoesNotExist_thenThrowReservationNotFoundException() {
        // Given
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationById(RESERVATION_ID));
    }

    // Tests for getUserReservations method

    @Test
    void getUserReservations_shouldReturnUserReservations() {
        // Given
        List<Reservation> userReservations = Arrays.asList(reservation);
        when(reservationRepository.findByUserId(USER_ID)).thenReturn(userReservations);

        // When
        List<Reservation> result = reservationService.getUserReservations(USER_ID);

        // Then
        assertEquals(userReservations, result);
    }

    // Tests for getActiveUserReservations method

    @Test
    void getActiveUserReservations_shouldReturnActiveUserReservations() {
        // Given
        List<Reservation> activeReservations = Arrays.asList(reservation);
        when(reservationRepository.findByUserIdAndStatusInOrderByQueuePosition(eq(USER_ID), anyList()))
                .thenReturn(activeReservations);

        // When
        List<Reservation> result = reservationService.getActiveUserReservations(USER_ID);

        // Then
        assertEquals(activeReservations, result);
    }

    // Tests for getBookReservations method

    @Test
    void getBookReservations_shouldReturnBookReservations() {
        // Given
        List<Reservation> bookReservations = Arrays.asList(reservation);
        when(reservationRepository.findByBookId(BOOK_ID)).thenReturn(bookReservations);

        // When
        List<Reservation> result = reservationService.getBookReservations(BOOK_ID);

        // Then
        assertEquals(bookReservations, result);
    }

    // Tests for getActiveBookReservations method

    @Test
    void getActiveBookReservations_shouldReturnActiveBookReservations() {
        // Given
        List<Reservation> activeReservations = Arrays.asList(reservation);
        when(reservationRepository.findByBookIdAndStatusInOrderByQueuePosition(eq(BOOK_ID), anyList()))
                .thenReturn(activeReservations);

        // When
        List<Reservation> result = reservationService.getActiveBookReservations(BOOK_ID);

        // Then
        assertEquals(activeReservations, result);
    }

    // Tests for isBookReservedForUser method

    @Test
    void isBookReservedForUser_whenBookIsReservedForUser_thenReturnTrue() {
        // Given
        when(reservationRepository.existsByBookIdAndUserIdAndStatusIn(eq(BOOK_ID), eq(USER_ID), anyList()))
                .thenReturn(true);

        // When
        boolean result = reservationService.isBookReservedForUser(BOOK_ID, USER_ID);

        // Then
        assertTrue(result);
    }

    @Test
    void isBookReservedForUser_whenBookIsNotReservedForUser_thenReturnFalse() {
        // Given
        when(reservationRepository.existsByBookIdAndUserIdAndStatusIn(eq(BOOK_ID), eq(USER_ID), anyList()))
                .thenReturn(false);

        // When
        boolean result = reservationService.isBookReservedForUser(BOOK_ID, USER_ID);

        // Then
        assertFalse(result);
    }

    // Tests for countActiveReservations method

    @Test
    void countActiveReservations_shouldReturnCountOfActiveReservations() {
        // Given
        int activeCount = 5;
        when(reservationRepository.countByBookIdAndStatusIn(eq(BOOK_ID), anyList())).thenReturn(activeCount);

        // When
        int result = reservationService.countActiveReservations(BOOK_ID);

        // Then
        assertEquals(activeCount, result);
    }

    // Tests for updateQueuePositions method (private method testing through public methods)

    @Test
    void cancelReservation_shouldUpdateQueuePositions() {
        // Given
        Reservation reservationToCancel = new Reservation(BOOK_ID, USER_ID, ReservationStatus.PENDING, 2);
        Reservation pendingReservation1 = new Reservation(BOOK_ID, 3L, ReservationStatus.PENDING, 1);
        Reservation pendingReservation2 = new Reservation(BOOK_ID, 4L, ReservationStatus.PENDING, 3);

        when(reservationRepository.saveReservation(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.findByBookIdAndStatusOrderByQueuePosition(BOOK_ID, ReservationStatus.PENDING))
                .thenReturn(Arrays.asList(pendingReservation1, pendingReservation2));

        // When
        reservationService.cancelReservation(reservationToCancel);

        // Then
        assertEquals(1, pendingReservation1.getQueuePosition());
        assertEquals(2, pendingReservation2.getQueuePosition());
        verify(reservationRepository).saveReservation(pendingReservation1);
        verify(reservationRepository).saveReservation(pendingReservation2);
    }
}