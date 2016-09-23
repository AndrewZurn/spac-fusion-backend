package com.zalude.spac.fusion.models.response;

import com.zalude.spac.fusion.models.domain.Exercise;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.models.domain.WorkoutWithDate;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Contains the information relevant to the workout that a user has completed. Contains an aggregation of
 * @see {@link WorkoutWithDate}, @see {@link Workout},
 * and @see {@link Exercise} information.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCompletedWorkoutResponse {
  @NotNull private UUID id;
  @NonNull private WorkoutWithDate workout;
  @NonNull private String result;
}
