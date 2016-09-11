package com.zalude.spac.fusion.models.request;

import lombok.*;

import javax.validation.constraints.NotNull;
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
public class UserCompletedWorkoutRequest {

  @NotNull
  private List<CompletedExerciseResultRequest> results;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CompletedExerciseResultRequest {

    private UUID lookupId;

    @NotNull
    private UUID exerciseOptionId;

    @NotNull
    private String result;

  }
  
}
