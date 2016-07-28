package com.zalude.spac.fusion.models.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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
public class UserCompletedWorkoutResponse {

  @NonNull
  private UUID workoutId;

  @NonNull
  private String duration;

  @NonNull
  private LocalDateTime completedDate;

  @NonNull
  private UUID exerciseId;

  @NonNull
  private String exerciseName;

  private String exerciseDescription;

  @NonNull
  private List<CompletedExerciseOptionResponse> results;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CompletedExerciseOptionResponse {

    @NonNull
    private UUID exerciseOptionId;

    @NonNull
    private String name;

    private String description;

    @NonNull
    private String type;

    @NonNull
    private String targetAmount;

    @NonNull
    private String amountCompleted;
  }
}
