package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.ProjectMemberResponse;
import com.techtribe.techtribe_backend.service.ProjectMemberService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService memberService;

    public ProjectMemberController(
            ProjectMemberService memberService) {

        this.memberService = memberService;
    }

    // GET PROJECT MEMBERS

    @GetMapping
    public ResponseEntity<?> getProjectMembers(
            @PathVariable Long projectId,
            Authentication authentication) {

        try {

            List<ProjectMemberResponse> members =
                    memberService.getProjectMembers(
                            authentication.getName(),
                            projectId
                    );

            return ResponseEntity.ok(members);

        } catch (SecurityException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}