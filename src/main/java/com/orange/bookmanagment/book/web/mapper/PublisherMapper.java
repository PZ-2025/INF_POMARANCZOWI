package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.web.model.PublisherDto;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {
    public PublisherDto toDto(Publisher publisher) {
        return new PublisherDto(
                publisher.getName(),
                publisher.getDescription()
        );
    }
}
