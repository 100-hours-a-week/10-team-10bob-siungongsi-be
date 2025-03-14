package org.bob.siungongsi.controller;

import java.util.List;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.controller.dto.AuthResponse.LoginSuccessResponse;
import org.bob.siungongsi.controller.dto.TermsResponse;
import org.bob.siungongsi.controller.spec.AuthControllerSpec;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth") // 회원 관련 API의 기본 경로
public class AuthController implements AuthControllerSpec {

  @Override
  @PostMapping("/register")
  public ResponseEntity<ApiResponseWrapper<?>> registerUser(@RequestBody AuthRequest authRequest) {

    String socialId = authRequest.socialId();
    String accessToken = authRequest.accessToken();

    if (socialId == null || socialId.isBlank() || socialId.length() > 100) {
      return ResponseEntity.status(401)
          .body(ApiResponseWrapper.error(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION));
    }

    return ResponseEntity.status(201)
        .body(ApiResponseWrapper.success(ApiResponseCode.AUTH_REGISTER_SUCCESS));
  }

  @Override
  @PostMapping("/login")
  public ResponseEntity<ApiResponseWrapper<LoginSuccessResponse>> loginUser(
      @RequestBody AuthRequest authRequest) {

    String accessToken = authRequest.accessToken();
    if (accessToken == null || accessToken.isBlank()) {
      return ResponseEntity.status(401)
          .body(ApiResponseWrapper.error(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION));
    }

    return ResponseEntity.ok(
        ApiResponseWrapper.success(
            ApiResponseCode.AUTH_LOGIN_SUCCESS, LoginSuccessResponse.of(accessToken)));
  }

  @Override
  @GetMapping("/terms")
  public ResponseEntity<ApiResponseWrapper<?>> getTerms() {
    return ResponseEntity.ok(
        ApiResponseWrapper.success(
            ApiResponseCode.AUTH_GET_TERMS_SUCCESS,
            List.of(
                TermsResponse.of(1, "이용약관 (필수)", "약관 내용"),
                TermsResponse.of(2, "개인정보 수집 및 이용 (필수)", "약관 내용"))));
  }

  @Override
  @DeleteMapping("/withdraw")
  public ResponseEntity<ApiResponseWrapper<?>> withdrawUser(
      @RequestHeader("Authorization") String authorization) {

    return ResponseEntity.ok(ApiResponseWrapper.success(ApiResponseCode.AUTH_LOGIN_SUCCESS, null));
  }
}
