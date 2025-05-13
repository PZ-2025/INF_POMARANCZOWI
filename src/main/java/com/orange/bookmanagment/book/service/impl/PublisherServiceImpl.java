package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.repository.PublisherRepository;
import com.orange.bookmanagment.book.service.PublisherService;
import com.orange.bookmanagment.book.web.requests.PublisherCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementacja serwisu {@link PublisherService}.
 * <p>
 * Odpowiada za tworzenie wydawców.
 */
@Service
@RequiredArgsConstructor
class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    /**
     * Tworzy nowego wydawcę na podstawie żądania.
     *
     * @param publisherCreateRequest dane nowego wydawcy
     * @return utworzona encja wydawcy
     */
    @Override
    public Publisher createPublisher(PublisherCreateRequest publisherCreateRequest) {
        final Publisher publisher = new Publisher(publisherCreateRequest.name(), publisherCreateRequest.description());
        return publisherRepository.savePublisher(publisher);
    }
}
