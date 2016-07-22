package com.zalude.spac.fusion.exceptions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
public class ResourceValidationException extends Exception {

  private Map<UUID, List<String>> errorsById;
  private Map<String, List<String>> errorsByName;

  public ResourceValidationException() {
  }

  public ResourceValidationException(String message) {
    super(message);
  }

  public ResourceValidationException(Map<UUID, List<String>> errorsById, Map<String, List<String>> errorsByName, String message) {
    super(message);
    this.errorsById = errorsById;
    this.errorsByName = errorsByName;
  }
}
