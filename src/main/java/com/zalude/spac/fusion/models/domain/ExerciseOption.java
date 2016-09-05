package com.zalude.spac.fusion.models.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

/**
 * Contains the information about a given option of an @see com.zalude.spac.models.domain.Exercise, such as
 * a name of an exercise option (full push-up or knee push-up).f
 *
 * @author Andrew Zurn (azurn)
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"exercise", "alternativeExerciseOption"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExerciseOption {

  @Id
  @Type(type = "pg-uuid")
  @NonNull
  private UUID id;

  @NonNull
  private String name;

  private String description;

  @NonNull
  private String type;

  private String targetAmount;  // NOTE: Most exerciseOptions should either be time (duration) or rep (targetAmount) based.

  private String duration;

  @OneToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="alternative_exercise_option_id")
  private ExerciseOption alternativeExerciseOption;

  @ManyToOne
  @JoinColumn(name = "exercise_id")
  @JsonBackReference
  private Exercise exercise;

  @Getter
  public enum ExerciseOptionType {
    AMRAP("AMRAP", "number"),
    TASK("Task", "time"),
    HEAVY("Heavy", "number"),
    CARDIO("Cardio", "NA"),
    THIRTY_THIRTY("30:30", "NA"),
    TWENTY_TEN("20:10", "NA");

    private String value;
    private String inputType;

    ExerciseOptionType(String value, String inputType) {
      this.value = value;
      this.inputType = inputType;
    }

    public static ExerciseOptionType fromValue(String value) {
      for (ExerciseOptionType type : ExerciseOptionType.values()) {
        if (value.equalsIgnoreCase(type.value)) {
          return type;
        }
      }
      throw new IllegalArgumentException("ExerciseOption of value: " + value + " does not exist.");
    }
  }

  @Tolerate
  public ExerciseOption() {}

}
