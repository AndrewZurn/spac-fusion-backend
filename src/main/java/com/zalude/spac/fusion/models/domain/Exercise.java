package com.zalude.spac.fusion.models.domain;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

/**
 * Contains a set of Exercise Options (such as handstand/full/knee push-ups), and other information about
 * a given exercise to be performed (duration, reps during time limit, etc.)
 *
 * @author Andrew Zurn (azurn)
 */
@lombok.Getter
@lombok.Setter
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"exerciseOptions", "duration"})
@lombok.ToString
public class Exercise {

    private final UUID id;
    private final Set<ExerciseOption> exerciseOptions;
    private final ExerciseActivity exerciseActivity;

}
