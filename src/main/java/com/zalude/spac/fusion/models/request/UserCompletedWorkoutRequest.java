package com.zalude.spac.fusion.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotNull
    private UUID exerciseOptionId;

    @NotNull
    private String result;

  }
  
}
