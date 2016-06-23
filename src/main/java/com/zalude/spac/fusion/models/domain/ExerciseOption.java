package com.zalude.spac.fusion.models.domain;

import java.util.UUID;

/**
 * Contains the information about a given option of an @see com.zalude.spac.models.domain.Exercise, such as
 * a name of an exercise option (full push-up or knee push-up).
 *
 * @author Andrew Zurn (azurn)
 */
@lombok.Getter
@lombok.Setter
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"name", "description"})
@lombok.ToString
public class ExerciseOption {

    private final UUID id;
    private final String name;
    private final String description;
}
