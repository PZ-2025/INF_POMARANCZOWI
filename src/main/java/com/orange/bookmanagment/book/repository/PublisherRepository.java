package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PublisherRepository {
    private final PublisherJpaRepository publisherJpaRepository;

    public Publisher savePublisher(Publisher publisher) {
        return publisherJpaRepository.save(publisher);
    }

    public Publisher findPublisherById(Long id) {
        return publisherJpaRepository.findById(id).orElse(null);
    }

}
