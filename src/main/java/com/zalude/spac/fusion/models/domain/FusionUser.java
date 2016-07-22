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

    @JsonIgnore
    @NotNull
    private String password;

    @NotNull
    private Integer age;

    @NotNull
    private Double height;

    @NotNull
    private Integer programLevel;

    @NotNull
    private Boolean activeStatus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
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
            return valueOf(Integer.toString(level));
        }
    }

    @Tolerate
    public FusionUser() {}

    public FusionUser(UUID id, String firstName, String lastName, String username, String email, String password, Integer age, Double height, Integer programLevel, Boolean activeStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
        this.height = height;
        this.programLevel = programLevel;
        this.activeStatus = activeStatus;
    }
}
