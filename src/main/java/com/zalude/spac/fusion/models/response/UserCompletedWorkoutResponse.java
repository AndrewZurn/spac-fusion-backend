package com.zalude.spac.fusion.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zalude.spac.fusion.models.domain.Exercise;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.models.domain.ScheduledWorkout;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Contains the information relevant to the scheduledWorkout that a user has completed. Contains an aggregation of
 * @see {@link ScheduledWorkout}, @see {@link Workout},
 * and @see {@link Exercise} information.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserCompletedWorkoutResponse {
  @NotNull private UUID id;
  @NonNull private ScheduledWorkout scheduledWorkout;
  @NonNull private String result;
}
