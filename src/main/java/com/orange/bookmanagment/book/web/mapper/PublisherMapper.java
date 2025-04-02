package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.web.model.PublisherDto;
import org.springframework.stereotype.Component;

/**
 * PublisherMapper is a component that maps Publisher entities to PublisherDto objects.
 * It is used to convert Publisher data for API responses.
 */
@Component
public class PublisherMapper {

    /**
     * Converts a Publisher entity to a PublisherDto object.
     *
     * @param publisher the Publisher entity to convert
     * @return the converted PublisherDto object
     */
    public PublisherDto toDto(Publisher publisher) {
        return new PublisherDto(
                publisher.getName(),
                publisher.getDescription()
        );
    }
}
