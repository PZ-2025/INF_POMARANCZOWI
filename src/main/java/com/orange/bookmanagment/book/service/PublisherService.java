package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.web.requests.PublisherCreateRequest;

public interface PublisherService {
    Publisher createPublisher(PublisherCreateRequest publisherCreateRequest);
}
