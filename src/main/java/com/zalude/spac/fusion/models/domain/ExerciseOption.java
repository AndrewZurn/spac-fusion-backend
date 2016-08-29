package com.zalude.spac.fusion.models.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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

  @NonNull
  private String targetAmount;

  @OneToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="alternative_exercise_option_id")
  private ExerciseOption alternativeExerciseOption;

  @ManyToOne
  @JoinColumn(name = "exercise_id")
  @JsonBackReference
  private Exercise exercise;

  @Tolerate
  ExerciseOption() {
  }

}
