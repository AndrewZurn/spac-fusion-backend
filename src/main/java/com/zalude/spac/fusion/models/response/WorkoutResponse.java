package com.zalude.spac.fusion.models.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@RequiredArgsConstructor
public class WorkoutResponse {

  @NonNull
  private UUID id;

  @NonNull
  private String duration;

  private String previewText;

  @NonNull
  private LocalDate workoutDate;

  @NonNull
  private ExerciseResponse exercise;

}
