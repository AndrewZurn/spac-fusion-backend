package com.zalude.spac.fusion.models.response;

import lombok.*;

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
public class UserCanUnlockWorkoutResponse {

  @NonNull
  private UUID userId;

  @NonNull
  private Boolean canUnlockWorkout;

  private int completedWorkoutsForWeek;

  private int allowedWorkoutsPerWeek;
}
