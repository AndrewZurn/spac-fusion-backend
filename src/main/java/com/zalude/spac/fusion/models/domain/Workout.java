package com.zalude.spac.fusion.models.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Contains a set of Exercise Options (such as handstand/full/knee push-ups), and other information about
 * a given exercise to be performed (duration, reps during time limit, etc.)
 *
 * @author Andrew Zurn (azurn)
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Workout {

  @Id
  @Type(type = "pg-uuid")
  @NotNull
  private UUID id;

  @NonNull
  private String name;

  private String instructions;

  private String duration; // if it is a time-based scheduledWorkout (such as AMRAP)

  @NonNull
  private String workoutType;

  // Denotes this exercise requires input for result.
  // This value should be used as it's appender (ie. 50 'lbs' to be saved as this exercises result.
  private String input;

  @OneToMany(mappedBy = "workout",
      cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      orphanRemoval = true)
  @NonNull
  @JsonManagedReference
  private List<Exercise> exercises;

  @Tolerate
  Workout() {
  }

  @Getter
  public enum WorkoutType {
    AMRAP("AMRAP", "number"),
    TASK("TASK", "time"),
    HEAVY("HEAVY", "number"),
    CARDIO("CARDIO", "NA"),
    THIRTY_THIRTY("30:30", "NA"),
    TWENTY_TEN("20:10", "NA");

    private String value;
    private String inputType;

    WorkoutType(String value, String inputType) {
      this.value = value;
      this.inputType = inputType;
    }

    public static WorkoutType fromValue(String value) {
      for (WorkoutType type : WorkoutType.values()) {
        if (value.equalsIgnoreCase(type.value)) {
          return type;
        }
      }
      throw new IllegalArgumentException("ExerciseOption of value: " + value + " does not exist.");
    }
  }

}
