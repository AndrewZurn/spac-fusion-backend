package com.zalude.spac.fusion.models.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Contains the information about a given option of an @see com.zalude.spac.models.domain.Exercise, such as
 * a name of an exercise option (full push-up or knee push-up).
 *
 * @author Andrew Zurn (azurn)
 */
@Data
@Entity
public class ExerciseOption {

    @Id
    private UUID id;

    private String name;

    private String description;

    private String type;

    private String targetAmount;

}
