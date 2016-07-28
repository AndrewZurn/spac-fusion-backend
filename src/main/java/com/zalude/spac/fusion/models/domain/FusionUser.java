package com.zalude.spac.fusion.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Contains information relevant to a user/customer of Fusion by SPAC. Holds details
 * such as a person's age, weight, program level, etc.
 *
 * @author Andrew Zurn (azurn)
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "userExerciseOptionLookups")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FusionUser {

  @Id
  @Type(type = "pg-uuid")
  @NonNull
  private UUID id;

  @NonNull
  private String firstName;

  @NonNull
  private String lastName;

  @NonNull
  private String username;

  @NonNull
  private String email;

  @NotNull
  private Integer age;

  @NotNull
  private Double height;

  @NotNull
  private Double weight;

  @NotNull
  private Integer programLevel;

  @NotNull
  private Boolean activeStatus;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<UserExerciseOptionLookup> userExerciseOptionLookups;

  public enum ProgramLevel {
    GOLD(2, "Gold"), SILVER(1, "Silver"), BRONZE(0, "Bronze");

    private int level;
    private String name;

    private ProgramLevel(int level, String name) {
      this.level = level;
      this.name = name;
    }

    public ProgramLevel fromValue(int level) {
      switch (level) {
        case 0:
          return ProgramLevel.BRONZE;
        case 1:
          return ProgramLevel.SILVER;
        case 2:
          return ProgramLevel.GOLD;
        default:
          throw new UnsupportedOperationException("Unsupported Program Level. Value: " + level);
      }
    }

    public ProgramLevel fromValue(String level) {
      switch (level.toUpperCase()) {
        case "BRONZE":
          return ProgramLevel.BRONZE;
        case "SILVER":
          return ProgramLevel.SILVER;
        case "GOLD":
          return ProgramLevel.GOLD;
        default:
          throw new UnsupportedOperationException("Unsupported Program Level. Value: " + level);
      }
    }
  }

  @Tolerate
  public FusionUser() {
  }

  public FusionUser(UUID id, String firstName, String lastName, String username, String email, Integer age, Double height, Double weight, Integer programLevel, Boolean activeStatus) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.email = email;
    this.age = age;
    this.height = height;
    this.weight = weight;
    this.programLevel = programLevel;
    this.activeStatus = activeStatus;
  }
}
