package ru.astondevs.socialnetwork.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationDto(@NotBlank(message = "Parameter 'firstName' must not be null or empty")
                                  @Size(max = 30, message = "Parameter 'firstName' length must be no more than 30 chars")
                                  String firstName,

                                  @NotBlank(message = "Parameter 'lastName' must not be null or empty")
                                  @Size(max = 30, message = "Parameter 'lastName' length must be no more than 30 chars")
                                  String lastName,

                                  @NotBlank(message = "Parameter 'email' must not be null or empty")
                                  @Email(message = "Incorrect 'email' parameter")
                                  String email,

                                  @NotBlank(message = "Parameter 'password' must not be null or empty")
                                  String password) {
}