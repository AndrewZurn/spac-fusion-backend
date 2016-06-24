package com.zalude.spac.fusion.models.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Data
@Entity
public class UserExerciseOptionLookup {

    @Id
    private UUID id;

    private UUID userId;

    private UUID exerciseOptionId;

    private String amountCompleted;

}
