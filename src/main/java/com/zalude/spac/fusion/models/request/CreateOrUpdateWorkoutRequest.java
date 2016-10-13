package com.zalude.spac.fusion.models.request;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The request representation to create or update an exercise within the system.
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateWorkoutRequest {

  @NotNull
  @NotBlank(message = "Please provide a name.")
  private String name;

  @NotNull
  private String instructions;

  private String duration;

  @NotBlank(message = "Please provide the type for the workout (AMRAP, TASK, HEAVE, 30:20, 20:10).")
  private String workoutType;
  
  private String input;

  private List<CreateOrUpdateExerciseRequest> exercises;
}
