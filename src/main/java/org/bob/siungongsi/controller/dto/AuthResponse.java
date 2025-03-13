package org.bob.siungongsi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthResponse {

  public record LoginSuccessResponse(
      @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
          String accessToken) {
    public static LoginSuccessResponse of(String accessToken) {
      return new LoginSuccessResponse(accessToken);
    }
  }
}
