package com.zalude.spac.fusion.models.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

/**
 * Contains a set of exercises that will be performed in order to achieve the 'daily' workout.
 *
 * @author Andrew Zurn (azurn)
 */
@Data
@Entity
public class Workout {

    @Id
    private UUID id;

    //private final Set<Exercise> exerciseSet;

    private Duration duration;

}
