package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.web.requests.PublisherCreateRequest;

/**
 * PublisherService interface for managing publishers.
 * This interface defines the contract for publishers management operations.
 */
public interface PublisherService {

    /**
     * Creates a new publisher based on the provided request.
     *
     * @param publisherCreateRequest the request containing publisher details
     * @return the created Publisher object
     */
    Publisher createPublisher(PublisherCreateRequest publisherCreateRequest);
}
