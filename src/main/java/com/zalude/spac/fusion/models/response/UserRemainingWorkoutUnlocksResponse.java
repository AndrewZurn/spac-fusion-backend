package com.zalude.spac.fusion.models.response;

import lombok.*;

import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRemainingWorkoutUnlocksResponse {

  @NonNull
  private UUID userId;

  private int remainingWorkoutUnlocks;
}
