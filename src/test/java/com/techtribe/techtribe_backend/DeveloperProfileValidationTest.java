package com.techtribe.techtribe_backend;

import com.techtribe.techtribe_backend.dto.DeveloperProfileRequest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeveloperProfileValidationTest {

    @Test
    void shouldRejectInvalidGithubUrl() {

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            Validator validator = factory.getValidator();

            DeveloperProfileRequest request =
                    new DeveloperProfileRequest();

            request.setBio("Java developer");
            request.setSkills("Java, Spring Boot");
            request.setGithubUrl(
                    "https://invalid-website.com/profile"
            );
            request.setLinkedinUrl(
                    "https://www.linkedin.com/in/testdeveloper"
            );

            var violations = validator.validate(request);

            assertTrue(
                    violations.stream().anyMatch(
                            violation -> violation
                                    .getPropertyPath()
                                    .toString()
                                    .equals("githubUrl")
                    ),
                    "Invalid GitHub URL should be rejected"
            );
        }
    }
}
