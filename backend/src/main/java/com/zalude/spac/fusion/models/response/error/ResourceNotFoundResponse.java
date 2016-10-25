package com.zalude.spac.fusion.models.response.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Data
@AllArgsConstructor
public class ResourceNotFoundResponse {

    @NotNull
    private UUID id;
}
