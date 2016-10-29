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
public class CreateOrUpdateScheduledWorkoutRequest {

  @NonNull
  private UUID workoutId;

  @NonNull
  private LocalDate workoutDate;
}
