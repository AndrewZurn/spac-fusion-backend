package com.zalude.spac.fusion.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateExerciseOptionRequest {

    private UUID id;

    @NotBlank(message = "Please provide a name for the exercise option.")
    private String name;

    private String description;

    @NotBlank(message = "Please provide the type that this exercise option is (ie. rep-based, time-based, etc.")
    private String type;

    @NotBlank(message = "Please provide the target amount for this exercise option (ie. 50 if rep-based, 2 mins if time-based, etc.)")
    private String targetAmount;

    private CreateOrUpdateExerciseOptionRequest alternativeExerciseOption;
}
