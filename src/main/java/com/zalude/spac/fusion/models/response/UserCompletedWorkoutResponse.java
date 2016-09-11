package com.zalude.spac.fusion.models.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Contains the information relevant to the workout that a user has completed. Contains an aggregation of
 * @see {@link com.zalude.spac.fusion.models.domain.Workout}, @see {@link com.zalude.spac.fusion.models.domain.Exercise},
 * and @see {@link com.zalude.spac.fusion.models.domain.ExerciseOption} information.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCompletedWorkoutResponse {

  @NonNull private UUID workoutId;
  @NonNull private String duration;
  @NonNull private LocalDate completedDate;
  @NonNull private CompletedExerciseResponse exercise;
  private String previewText;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CompletedExerciseResponse {
    @NonNull private UUID id;
    @NonNull private String name;
    @NonNull private String instructions;
    @NonNull private List<CompletedExerciseOptionResponse> results;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CompletedExerciseOptionResponse {
    @NonNull private UUID lookupId;
    @NonNull private UUID exerciseOptionId;
    @NonNull private String name;
    @NonNull private String type;
    @NonNull private String result; // should be the result/score of the user to be compared against the targetAmount.
    private String description;
    private String targetAmount;
    private String duration;
  }
}
