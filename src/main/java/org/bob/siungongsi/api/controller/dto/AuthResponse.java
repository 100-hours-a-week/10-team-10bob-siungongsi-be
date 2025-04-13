package org.bob.siungongsi.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthResponse {
  public record LoginSuccessResponse(
      @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
          String accessToken,
      String refreshToken,
      boolean isUser) {
    public static LoginSuccessResponse of(String accessToken, String refreshToken, boolean isUser) {
      return new LoginSuccessResponse(accessToken, refreshToken, isUser);
    }
  }

  public record RegisterSuccessResponse(
      @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
          String accessToken,
      String refreshToken) {
    public static RegisterSuccessResponse of(String accessToken, String refreshToken) {
      return new RegisterSuccessResponse(accessToken, refreshToken);
    }
  }
}
