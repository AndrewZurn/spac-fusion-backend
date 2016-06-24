package com.zalude.spac.fusion.models.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;
import java.util.UUID;

/**
 * Contains a set of Exercise Options (such as handstand/full/knee push-ups), and other information about
 * a given exercise to be performed (duration, reps during time limit, etc.)
 *
 * @author Andrew Zurn (azurn)
 */
@Data
@Entity
public class Exercise {

    @Id
    private UUID id;

    //private final Set<ExerciseOption> exerciseOptions;

}
