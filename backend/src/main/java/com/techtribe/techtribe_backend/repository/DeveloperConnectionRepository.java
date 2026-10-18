package com.techtribe.techtribe_backend.repository;

import com.techtribe.techtribe_backend.model.DeveloperConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloperConnectionRepository
        extends JpaRepository<DeveloperConnection, Long> {

    Optional<DeveloperConnection>
    findByRequesterIdAndReceiverId(
            Long requesterId,
            Long receiverId
    );

    List<DeveloperConnection>
    findByReceiverIdAndStatus(
            Long receiverId,
            String status
    );

    List<DeveloperConnection>
    findByRequesterIdAndStatus(
            Long requesterId,
            String status
    );

    List<DeveloperConnection>
    findByRequesterIdOrReceiverId(
            Long requesterId,
            Long receiverId
    );
}