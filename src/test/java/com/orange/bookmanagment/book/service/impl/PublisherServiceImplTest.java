package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.repository.PublisherRepository;
import com.orange.bookmanagment.book.web.requests.PublisherCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Captor
    private ArgumentCaptor<Publisher> publisherCaptor;

    private PublisherCreateRequest publisherCreateRequest;
    private Publisher savedPublisher;
    private static final String PUBLISHER_NAME = "Test Publisher";
    private static final String PUBLISHER_DESCRIPTION = "Test Publisher Description";
    private static final Long PUBLISHER_ID = 1L;

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych
        publisherCreateRequest = new PublisherCreateRequest(PUBLISHER_NAME, PUBLISHER_DESCRIPTION);

        savedPublisher = new Publisher(PUBLISHER_NAME, PUBLISHER_DESCRIPTION);
        savedPublisher.setId(PUBLISHER_ID);
    }

    @Test
    void createPublisher_shouldCreateAndSavePublisher() {
        // given
        when(publisherRepository.savePublisher(any(Publisher.class))).thenReturn(savedPublisher);

        // when
        Publisher result = publisherService.createPublisher(publisherCreateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(PUBLISHER_ID);
        assertThat(result.getName()).isEqualTo(PUBLISHER_NAME);
        assertThat(result.getDescription()).isEqualTo(PUBLISHER_DESCRIPTION);

        verify(publisherRepository).savePublisher(publisherCaptor.capture());

        Publisher capturedPublisher = publisherCaptor.getValue();
        assertThat(capturedPublisher.getName()).isEqualTo(PUBLISHER_NAME);
        assertThat(capturedPublisher.getDescription()).isEqualTo(PUBLISHER_DESCRIPTION);
    }

    @Test
    void createPublisher_withNullDescription_shouldCreatePublisherWithNullDescription() {
        // given
        PublisherCreateRequest requestWithNullDesc = new PublisherCreateRequest(PUBLISHER_NAME, null);
        Publisher publisherWithNullDesc = new Publisher(PUBLISHER_NAME, null);
        publisherWithNullDesc.setId(PUBLISHER_ID);

        when(publisherRepository.savePublisher(any(Publisher.class))).thenReturn(publisherWithNullDesc);

        // when
        Publisher result = publisherService.createPublisher(requestWithNullDesc);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(PUBLISHER_ID);
        assertThat(result.getName()).isEqualTo(PUBLISHER_NAME);
        assertThat(result.getDescription()).isNull();

        verify(publisherRepository).savePublisher(publisherCaptor.capture());

        Publisher capturedPublisher = publisherCaptor.getValue();
        assertThat(capturedPublisher.getName()).isEqualTo(PUBLISHER_NAME);
        assertThat(capturedPublisher.getDescription()).isNull();
    }

    @Test
    void createPublisher_withEmptyDescription_shouldCreatePublisherWithEmptyDescription() {
        // given
        PublisherCreateRequest requestWithEmptyDesc = new PublisherCreateRequest(PUBLISHER_NAME, "");
        Publisher publisherWithEmptyDesc = new Publisher(PUBLISHER_NAME, "");
        publisherWithEmptyDesc.setId(PUBLISHER_ID);

        when(publisherRepository.savePublisher(any(Publisher.class))).thenReturn(publisherWithEmptyDesc);

        // when
        Publisher result = publisherService.createPublisher(requestWithEmptyDesc);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(PUBLISHER_ID);
        assertThat(result.getName()).isEqualTo(PUBLISHER_NAME);
        assertThat(result.getDescription()).isEmpty();

        verify(publisherRepository).savePublisher(publisherCaptor.capture());

        Publisher capturedPublisher = publisherCaptor.getValue();
        assertThat(capturedPublisher.getName()).isEqualTo(PUBLISHER_NAME);
        assertThat(capturedPublisher.getDescription()).isEmpty();
    }
}