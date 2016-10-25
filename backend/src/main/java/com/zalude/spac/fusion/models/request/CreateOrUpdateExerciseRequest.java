package com.zalude.spac.fusion.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateExerciseRequest {

  private UUID id;

  @NotBlank(message = "Please provide a name for the exercise option.")
  private String name;

  private String amount;

  private String duration;

  // Denotes this exercise requires input for result.
  // This value should be used as it's appender (ie. 50 'lbs' to be saved as this exercises result.
  private String input;
}
