package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.CodeSnippetRequest;
import com.techtribe.techtribe_backend.dto.CodeSnippetResponse;
import com.techtribe.techtribe_backend.model.CodeSnippet;
import com.techtribe.techtribe_backend.service.CodeSnippetService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/snippets")
public class CodeSnippetController {

    private final CodeSnippetService snippetService;

    public CodeSnippetController(CodeSnippetService snippetService) {
        this.snippetService = snippetService;
    }

    // =====================================
    // CREATE CODE SNIPPET
    // =====================================

    @PostMapping
    public ResponseEntity<?> createSnippet(
            @PathVariable Long projectId,
            @Valid @RequestBody CodeSnippetRequest request,
            Authentication authentication) {

        try {

            CodeSnippet snippet = snippetService.createSnippet(
                    authentication.getName(),
                    projectId,
                    request
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CodeSnippetResponse(snippet));

        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // VIEW ALL PROJECT SNIPPETS
    // =====================================

    @GetMapping
    public ResponseEntity<?> getProjectSnippets(
            @PathVariable Long projectId,
            Authentication authentication) {

        try {

            List<CodeSnippetResponse> snippets =
                    snippetService.getProjectSnippets(
                                    authentication.getName(),
                                    projectId
                            )
                            .stream()
                            .map(CodeSnippetResponse::new)
                            .toList();

            return ResponseEntity.ok(snippets);

        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // VIEW SINGLE CODE SNIPPET
    // =====================================

    @GetMapping("/{snippetId}")
    public ResponseEntity<?> getSnippet(
            @PathVariable Long projectId,
            @PathVariable Long snippetId,
            Authentication authentication) {

        try {

            CodeSnippet snippet = snippetService.getSnippetById(
                    authentication.getName(),
                    projectId,
                    snippetId
            );

            return ResponseEntity.ok(
                    new CodeSnippetResponse(snippet)
            );

        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // UPDATE CODE SNIPPET - AUTHOR ONLY
    // =====================================

    @PutMapping("/{snippetId}")
    public ResponseEntity<?> updateSnippet(
            @PathVariable Long projectId,
            @PathVariable Long snippetId,
            @Valid @RequestBody CodeSnippetRequest request,
            Authentication authentication) {

        try {

            CodeSnippet snippet = snippetService.updateSnippet(
                    authentication.getName(),
                    projectId,
                    snippetId,
                    request
            );

            return ResponseEntity.ok(
                    new CodeSnippetResponse(snippet)
            );

        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // DELETE CODE SNIPPET
    // AUTHOR OR PROJECT OWNER
    // =====================================

    @DeleteMapping("/{snippetId}")
    public ResponseEntity<?> deleteSnippet(
            @PathVariable Long projectId,
            @PathVariable Long snippetId,
            Authentication authentication) {

        try {

            snippetService.deleteSnippet(
                    authentication.getName(),
                    projectId,
                    snippetId
            );

            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}