package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.web.model.PublisherDto;
import org.springframework.stereotype.Component;

/**
 * Komponent odpowiedzialny za mapowanie encji {@link Publisher} do obiektu DTO {@link PublisherDto}.
 */
@Component
public class PublisherMapper {

    /**
     * Konwertuje encję wydawcy na obiekt DTO.
     *
     * @param publisher encja wydawcy
     * @return DTO wydawcy
     */
    public PublisherDto toDto(Publisher publisher) {
        return new PublisherDto(
                publisher.getName(),
                publisher.getDescription()
        );
    }
}
