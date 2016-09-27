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
@ToString(exclude = {"workout"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Exercise {

  @Id
  @Type(type = "pg-uuid")
  @NonNull
  private UUID id;

  @NonNull
  private String name;

  @NonNull
  private String amount; // example: 500 m, 200, 5x, 5x5

  @ManyToOne
  @JoinColumn(name = "workout_id")
  @JsonBackReference
  private Workout workout;

  @Tolerate
  public Exercise() {}

}
