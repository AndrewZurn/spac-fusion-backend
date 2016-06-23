package com.zalude.spac.fusion.models.domain;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

/**
 * Contains a set of exercises that will be performed in order to achieve the 'daily' workout.
 *
 * @author Andrew Zurn (azurn)
 */
@lombok.Getter
@lombok.Setter
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"name", "description"})
@lombok.ToString
public class Workout {

    private final UUID id;
    private final Set<Exercise> exerciseSet;
    private final Duration duration;

}
