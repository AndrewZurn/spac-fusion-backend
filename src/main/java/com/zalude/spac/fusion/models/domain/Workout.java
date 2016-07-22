package com.zalude.spac.fusion.models.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Contains a set of exercise that will be performed in order to achieve the 'daily' workout.
 *
 * @author Andrew Zurn (azurn)
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "exercise")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Workout {

  @Id
  @Type(type = "pg-uuid")
  @NonNull
  private UUID id;

  @NonNull
  private String duration;

  @NonNull
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime workoutDate;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "exercise_id")
  private Exercise exercise;

  @Tolerate
  Workout() {}

}
