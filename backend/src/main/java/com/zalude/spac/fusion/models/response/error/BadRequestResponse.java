package com.zalude.spac.fusion.models.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BadRequestResponse {

  private Map<UUID, List<String>> errorsById;
  private Map<String, List<String>> errorsByName;
  private String message;

  public BadRequestResponse(Map<UUID, List<String>> errorsById, Map<String, List<String>> errorsByName, String message) {
    this.errorsById = errorsById;
    this.errorsByName = errorsByName;
    this.message = message;
  }
}
