
package com.techtribe.techtribe_backend.repository;

import com.techtribe.techtribe_backend.model.ProjectJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectJoinRequestRepository
        extends JpaRepository<ProjectJoinRequest, Long> {

    Optional<ProjectJoinRequest> findByProjectIdAndUserId(
            Long projectId,
            Long userId
    );

    List<ProjectJoinRequest> findByProjectIdAndStatus(
            Long projectId,
            String status
    );

    boolean existsByProjectIdAndUserId(
            Long projectId,
            Long userId
    );
}