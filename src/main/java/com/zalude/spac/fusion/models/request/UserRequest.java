package com.zalude.spac.fusion.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class UserRequest {

  private String auth0Id;

  private String firstName;

  private String lastName;

  private String username;

  private String email;

  private Integer age;

  private Double height;

  private Double weight;

  private String programLevel;

}
