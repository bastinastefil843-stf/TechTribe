package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.CodeSnippetRequest;
import com.techtribe.techtribe_backend.model.CodeSnippet;
import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.CodeSnippetRepository;
import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CodeSnippetService {

    private final CodeSnippetRepository snippetRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public CodeSnippetService(
            CodeSnippetRepository snippetRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {

        this.snippetRepository = snippetRepository;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    // GET LOGGED-IN USER

    private User getAuthenticatedUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    // VERIFY PROJECT ACCESS

    private Project getAccessibleProject(String email, Long projectId) {

        User user = getAuthenticatedUser(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found"));

        boolean isOwner = project.getOwner().getId()
                .equals(user.getId());

        boolean isContributor =
                memberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isOwner && !isContributor) {
            throw new SecurityException(
                    "You are not a member of this project");
        }

        return project;
    }

    // CREATE SNIPPET

    @Transactional
    public CodeSnippet createSnippet(
            String email,
            Long projectId,
            CodeSnippetRequest request) {

        User author = getAuthenticatedUser(email);
        Project project = getAccessibleProject(email, projectId);

        CodeSnippet snippet = new CodeSnippet();

        snippet.setProject(project);
        snippet.setAuthor(author);
        snippet.setTitle(request.getTitle());
        snippet.setLanguage(request.getLanguage());
        snippet.setDescription(request.getDescription());
        snippet.setCode(request.getCode());

        return snippetRepository.save(snippet);
    }

    // VIEW ALL PROJECT SNIPPETS

    @Transactional(readOnly = true)
    public List<CodeSnippet> getProjectSnippets(
            String email,
            Long projectId) {

        getAccessibleProject(email, projectId);

        return snippetRepository.findByProjectId(projectId);
    }

    // VIEW SINGLE SNIPPET

    @Transactional(readOnly = true)
    public CodeSnippet getSnippetById(
            String email,
            Long projectId,
            Long snippetId) {

        getAccessibleProject(email, projectId);

        return snippetRepository
                .findByIdAndProjectId(snippetId, projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Snippet not found"));
    }

    // UPDATE SNIPPET - AUTHOR ONLY

    @Transactional
    public CodeSnippet updateSnippet(
            String email,
            Long projectId,
            Long snippetId,
            CodeSnippetRequest request) {

        User user = getAuthenticatedUser(email);

        CodeSnippet snippet = getSnippetById(
                email,
                projectId,
                snippetId
        );

        if (!snippet.getAuthor().getId().equals(user.getId())) {
            throw new SecurityException(
                    "Only the snippet author can update this snippet");
        }

        snippet.setTitle(request.getTitle());
        snippet.setLanguage(request.getLanguage());
        snippet.setDescription(request.getDescription());
        snippet.setCode(request.getCode());

        return snippetRepository.save(snippet);
    }

    // DELETE SNIPPET - AUTHOR OR PROJECT OWNER

    @Transactional
    public void deleteSnippet(
            String email,
            Long projectId,
            Long snippetId) {

        User user = getAuthenticatedUser(email);

        Project project = getAccessibleProject(email, projectId);

        CodeSnippet snippet = snippetRepository
                .findByIdAndProjectId(snippetId, projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Snippet not found"));

        boolean isAuthor = snippet.getAuthor().getId()
                .equals(user.getId());

        boolean isOwner = project.getOwner().getId()
                .equals(user.getId());

        if (!isAuthor && !isOwner) {
            throw new SecurityException(
                    "You cannot delete another developer's snippet");
        }

        snippetRepository.delete(snippet);
    }
}