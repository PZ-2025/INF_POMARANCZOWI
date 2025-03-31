package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherJpaRepository extends JpaRepository<Publisher, Long> {
    // Custom query methods can be defined here if needed
}
