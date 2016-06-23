package com.zalude.spac.fusion.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

/**
 * Contains information relevant to a user/customer of Fusion by SPAC. Holds details
 * such as a person's age, weight, program level, etc.
 *
 * @author Andrew Zurn (azurn)
 */
@lombok.Getter
@lombok.Setter
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"username", "email", "firstname", "lastname"})
@lombok.ToString(exclude = "password")
public class User {

    private UUID id;
    private String firstname;
    private String lastname;
    private String username;

    @JsonIgnore
    private String password;
    private String email;
    private int age;
    private double height;
    private ProgramLevel programLevel;
    private ActiveStatus activeStatus;

    enum ProgramLevel {
        GOLD, SILVER, BRONZE
    }

    enum ActiveStatus {
        ACTIVE, INACTIVE
    }
}
