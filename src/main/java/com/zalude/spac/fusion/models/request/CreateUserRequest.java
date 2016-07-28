package com.zalude.spac.fusion.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest extends UserRequest {

  @NotBlank(message = "Please provide a First Name for the user.")
  private String firstName;

  @NotBlank(message = "Please provide a Last Name for the user.")
  private String lastName;

  @NotBlank(message = "Please provide a User Name for the user.")
  private String username;

  @NotBlank(message = "Please provide a Email Address for the user.")
  private String email;

  @NotNull
  private Integer age;

  @NotNull
  private Double height;

  @NotNull
  private Double weight;

  @NotNull
  private Integer programLevel;

}