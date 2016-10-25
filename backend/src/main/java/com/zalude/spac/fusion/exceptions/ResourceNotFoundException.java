package com.zalude.spac.fusion.exceptions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
public class ResourceNotFoundException extends Exception {

  private UUID id;

  public ResourceNotFoundException() {
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(UUID id, String message) {
    super(message);
    this.id = id;
  }
}
