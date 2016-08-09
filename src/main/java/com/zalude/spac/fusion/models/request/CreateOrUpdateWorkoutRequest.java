package com.zalude.spac.fusion.models.request;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateWorkoutRequest {

  @NonNull
  private UUID exerciseId;

  @NonNull
  private String duration;

  @NonNull
  private LocalDate workoutDate;
}
