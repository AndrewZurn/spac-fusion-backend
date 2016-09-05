package com.zalude.spac.fusion.models.response;

import com.zalude.spac.fusion.models.domain.ExerciseOption;
import lombok.*;
import lombok.experimental.Tolerate;

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

  @NonNull
  private String inputType;

  private ExerciseOptionResponse alternativeExerciseOption;

  public ExerciseOptionResponse(UUID id, String name, String type, String duration,
                                String inputType, String targetAmount, ExerciseOptionResponse alternativeExerciseOption) {
    super(id, name, type);
    this.setTargetAmount(targetAmount);
    this.setDuration(duration);
    this.inputType = inputType;
    this.alternativeExerciseOption = alternativeExerciseOption;
  }

  @Tolerate
  public ExerciseOptionResponse() {
    super();
  }
}
