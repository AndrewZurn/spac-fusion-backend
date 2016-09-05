package com.zalude.spac.fusion.models.response;

import com.zalude.spac.fusion.models.domain.ExerciseOption;
import lombok.*;

import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@ToString
public class ExerciseOptionResponse extends ExerciseOption {
  private String dataType;
  private ExerciseOptionResponse alternativeExerciseOption;

  public ExerciseOptionResponse(UUID id, String name, String type, String duration,
                                String workoutDisplayType, String targetAmount, ExerciseOptionResponse alternativeExerciseOption) {
    super(id, name, type);
    this.setTargetAmount(targetAmount);
    this.setDuration(duration);
    this.dataType = workoutDisplayType;
    this.alternativeExerciseOption = alternativeExerciseOption;
  }
}
