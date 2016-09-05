package com.zalude.spac.fusion.models.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.GenericGenerator;
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
public class Exercise {

  @Id
  @Type(type = "pg-uuid")
  @NotNull
  private UUID id;

  @NonNull
  private String name;

  @NonNull
  private String instructions;

  @OneToMany(mappedBy = "exercise",
      cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      orphanRemoval = true)
  @NonNull
  @JsonManagedReference
  private List<ExerciseOption> exerciseOptions;

  @Tolerate
  Exercise() {
  }

}
