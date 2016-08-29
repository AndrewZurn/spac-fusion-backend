package com.zalude.spac.fusion.models.request;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Request to be sent from an Auth0 webhook upon user creation in their system.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest extends UserRequest {

  @NotBlank(message = "An Auth0 User ID must be provided.")
  private String auth0Id;

  @NotBlank(message = "Please provide a First Name for the user.")
  private String firstName;

  @NotBlank(message = "Please provide a Last Name for the user.")
  private String lastName;

  @NotBlank(message = "Please provide a User Name for the user.")
  private String username;

  @NotBlank(message = "Please provide a Email Address for the user.")
  private String email;

  @NotBlank(message = "A Program Level of 'GOLD', 'SILVER', or 'BRONZE' must be provided.")
  private String programLevel;

}