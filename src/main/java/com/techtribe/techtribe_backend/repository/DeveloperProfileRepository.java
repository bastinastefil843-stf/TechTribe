package com.techtribe.techtribe_backend.repository;

import com.techtribe.techtribe_backend.model.DeveloperProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloperProfileRepository
        extends JpaRepository<DeveloperProfile, Long> {

    // Existing functionality
    Optional<DeveloperProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    // Module 10: Developer directory
    @Query("""
        SELECT p FROM DeveloperProfile p
        JOIN FETCH p.user u
        WHERE u.id <> :currentUserId
        ORDER BY u.name ASC
        """)
    List<DeveloperProfile> findDirectoryProfiles(
            @Param("currentUserId") Long currentUserId
    );

    // Module 10: Search by name or skills
    @Query("""
        SELECT p FROM DeveloperProfile p
        JOIN FETCH p.user u
        WHERE u.id <> :currentUserId
        AND (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(p.skills, ''))
                LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        ORDER BY u.name ASC
        """)
    List<DeveloperProfile> searchDevelopers(
            @Param("currentUserId") Long currentUserId,
            @Param("keyword") String keyword
    );

    // Module 10: View public profile
    @Query("""
        SELECT p FROM DeveloperProfile p
        JOIN FETCH p.user u
        WHERE u.id = :userId
        """)
    Optional<DeveloperProfile> findPublicProfile(
            @Param("userId") Long userId
    );
}