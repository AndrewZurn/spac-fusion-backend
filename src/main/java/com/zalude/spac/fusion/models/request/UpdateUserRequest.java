package com.zalude.spac.fusion.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest extends UserRequest {

  public UpdateUserRequest(String firstName, String lastName, String username, String email, Integer age, Double height, Double weight, Integer programLevel) {
    super(firstName, lastName, username, email, age, height, weight, programLevel);
  }
}
