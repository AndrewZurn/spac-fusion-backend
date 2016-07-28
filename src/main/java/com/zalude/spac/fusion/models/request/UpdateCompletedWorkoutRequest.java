package com.zalude.spac.fusion.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompletedWorkoutRequest {

  private String amountCompleted;
}
