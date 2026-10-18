package com.techtribe.techtribe_backend.repository;

import com.techtribe.techtribe_backend.model.CodeSnippet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeSnippetRepository
        extends JpaRepository<CodeSnippet, Long> {

    // Find all snippets belonging to a project
    List<CodeSnippet> findByProjectId(Long projectId);

    // Find a specific snippet within a project
    Optional<CodeSnippet> findByIdAndProjectId(
            Long snippetId,
            Long projectId
    );

    // Find snippets created by a developer
    List<CodeSnippet> findByAuthorId(Long authorId);

    // Find snippets by programming language within a project
    List<CodeSnippet> findByProjectIdAndLanguageIgnoreCase(
            Long projectId,
            String language
    );
}