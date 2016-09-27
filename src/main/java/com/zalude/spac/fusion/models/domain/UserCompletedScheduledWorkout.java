package com.zalude.spac.fusion.models.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCompletedScheduledWorkout {

  @Id
  @Type(type = "pg-uuid")
  @NonNull
  private UUID id;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "user_id")
  private FusionUser user;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "scheduled_workout_id")
  private ScheduledWorkout scheduledWorkout;

  @NonNull
  private String result;

  @Tolerate
  public UserCompletedScheduledWorkout() {
  }
}
