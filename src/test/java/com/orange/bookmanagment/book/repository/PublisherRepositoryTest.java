package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherRepositoryTest {

    @Mock
    private PublisherJpaRepository publisherJpaRepository;

    @InjectMocks
    private PublisherRepository publisherRepository;

    private Publisher testPublisher;
    private static final Long PUBLISHER_ID = 1L;
    private static final String PUBLISHER_NAME = "Test Publisher";
    private static final String PUBLISHER_DESCRIPTION = "Test Publisher Description";

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych
        testPublisher = new Publisher(PUBLISHER_NAME, PUBLISHER_DESCRIPTION);
        testPublisher.setId(PUBLISHER_ID);
    }

    @Test
    void savePublisher_shouldDelegateToJpaRepository() {
        // given
        when(publisherJpaRepository.save(testPublisher)).thenReturn(testPublisher);

        // when
        Publisher result = publisherRepository.savePublisher(testPublisher);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testPublisher);
        assertThat(result.getId()).isEqualTo(PUBLISHER_ID);
        assertThat(result.getName()).isEqualTo(PUBLISHER_NAME);
        assertThat(result.getDescription()).isEqualTo(PUBLISHER_DESCRIPTION);

        verify(publisherJpaRepository).save(testPublisher);
    }

    @Test
    void findPublisherById_whenPublisherExists_shouldReturnPublisher() {
        // given
        when(publisherJpaRepository.findById(PUBLISHER_ID)).thenReturn(Optional.of(testPublisher));

        // when
        Publisher result = publisherRepository.findPublisherById(PUBLISHER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testPublisher);
        assertThat(result.getId()).isEqualTo(PUBLISHER_ID);
        assertThat(result.getName()).isEqualTo(PUBLISHER_NAME);
        assertThat(result.getDescription()).isEqualTo(PUBLISHER_DESCRIPTION);

        verify(publisherJpaRepository).findById(PUBLISHER_ID);
    }

    @Test
    void findPublisherById_whenPublisherDoesNotExist_shouldReturnNull() {
        // given
        when(publisherJpaRepository.findById(PUBLISHER_ID)).thenReturn(Optional.empty());

        // when
        Publisher result = publisherRepository.findPublisherById(PUBLISHER_ID);

        // then
        assertThat(result).isNull();

        verify(publisherJpaRepository).findById(PUBLISHER_ID);
    }
}