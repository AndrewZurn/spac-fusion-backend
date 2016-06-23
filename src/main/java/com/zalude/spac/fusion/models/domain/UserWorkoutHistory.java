package com.zalude.spac.fusion.models.domain;

import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@lombok.Getter
@lombok.Setter
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"userId", "workoutId"})
@lombok.ToString
public class UserWorkoutHistory {

    private final UUID id;
    private final UUID userId;
    private final UUID workoutId;
    // TODO: results???

}
