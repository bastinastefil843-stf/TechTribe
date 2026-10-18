package com.techtribe.techtribe_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "developer_profiles")
public class DeveloperProfile {

    // =====================================
    // 1. PROFILE ID
    // =====================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================
    // 2. USER RELATIONSHIP
    // One user can have one developer profile
    // =====================================

    @OneToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true
    )
    private User user;

    // =====================================
    // 3. DEVELOPER BIO
    // Maximum 2000 characters
    // =====================================

    @Column(length = 2000)
    private String bio;

    // =====================================
    // 4. TECHNICAL SKILLS
    // Maximum 1000 characters
    // =====================================

    @Column(length = 1000)
    private String skills;

    // =====================================
    // 5. GITHUB PROFILE URL
    // Maximum 255 characters
    // =====================================

    @Column(length = 255)
    private String githubUrl;

    // =====================================
    // 6. LINKEDIN PROFILE URL
    // Maximum 255 characters
    // =====================================

    @Column(length = 255)
    private String linkedinUrl;

    // =====================================
    // 7. DEFAULT CONSTRUCTOR
    // =====================================

    public DeveloperProfile() {
    }

    // =====================================
    // 8. GETTERS AND SETTERS
    // =====================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }
}