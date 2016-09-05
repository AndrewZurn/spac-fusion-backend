package com.zalude.spac.fusion.models.response;

import com.zalude.spac.fusion.models.domain.Exercise;
import com.zalude.spac.fusion.models.domain.ExerciseOption;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ExerciseResponse {

  @NonNull
  private UUID id;

  @NonNull
  private String name;

  @NonNull
  private String instructions;

  @NonNull
  private List<ExerciseOptionResponse> exerciseOptions;
}
