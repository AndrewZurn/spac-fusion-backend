package com.zalude.spac.fusion.models.request;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The request representation to create or update an exercise within the system.
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateExerciseRequest {

    @NotNull
    @NotBlank(message = "Please provide a name.")
    private String name;

    @NotNull
    private String description;

    private List<CreateOrUpdateExerciseOptionRequest> exerciseOptions;
}
