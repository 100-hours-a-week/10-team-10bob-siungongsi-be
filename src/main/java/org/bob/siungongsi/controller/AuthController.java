package org.bob.siungongsi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/auth") // 회원 관련 API의 기본 경로
@Tag(name = "회원 인증 API", description = "회원 로그인, 탈퇴 및 약관 조회 API") // Swagger에서 API 그룹 지정
public class AuthController {

	/** 회원 탈퇴 API DELETE /v1/auth/withdraw */
	@DeleteMapping("/withdraw")
	@Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제하는 API (설정 페이지에서 사용)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
		@ApiResponse(responseCode = "401", description = "JWT 토큰이 필요함"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<Map<String, Object>> withdrawUser(
			@Parameter(
							description = "JWT 토큰 (Bearer 포함)",
							required = true,
							example = "Bearer your_token_here")
					@RequestHeader("Authorization")
					String authorization) {

		return ResponseEntity.ok(Map.of("code", 9202, "message", "withdraw_user_success"));
	}

	/** 약관 조회 API GET /v1/auth/terms */
	@GetMapping("/terms")
	@Operation(summary = "약관 조회", description = "회원가입 시 필요한 약관 정보를 가져오는 API (회원가입 페이지에서 사용)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "약관 조회 성공"),
		@ApiResponse(responseCode = "404", description = "약관을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<Map<String, Object>> getTerms() {
		return ResponseEntity.ok(
				Map.of(
						"code",
						9202,
						"message",
						"get_terms_success",
						"data",
						List.of(
								Map.of(
										"termsId",
										1,
										"termsTitle",
										"이용약관 (필수)",
										"termsContent",
										"약관 내용"),
								Map.of(
										"termsId",
										2,
										"termsTitle",
										"개인정보 수집 및 이용 (필수)",
										"termsContent",
										"약관 내용"))));
	}

	/** 로그인 API POST /v1/auth/login */
	@PostMapping("/login")
	@Operation(summary = "로그인", description = "회원 인증 후 JWT 토큰을 반환하는 API (로그인 페이지에서 사용)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "401", description = "액세스 토큰 만료 또는 인증 필요"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<Map<String, Object>> loginUser(
			@RequestBody Map<String, String> requestBody) {

		String accessToken = requestBody.get("accessToken");
		if (accessToken == null || accessToken.isBlank()) {
			return ResponseEntity.status(401)
					.body(Map.of("code", 9402, "message", "required_authorization"));
		}

		return ResponseEntity.ok(
				Map.of("code", 9203, "message", "login_success", "accessToken", accessToken));
	}

	/** 회원가입 API POST /v1/auth/register */
	@PostMapping("/register")
	@Operation(summary = "회원가입", description = "회원 정보를 등록하는 API (회원가입 페이지에서 사용)")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원가입 성공"),
		@ApiResponse(responseCode = "401", description = "액세스 토큰 만료"),
		@ApiResponse(responseCode = "401", description = "JWT 인증 필요"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<Map<String, Object>> registerUser(
			@RequestBody Map<String, String> requestBody) {

		String socialId = requestBody.get("socialId");
		String accessToken = requestBody.get("accessToken");

		if (socialId == null || socialId.isBlank() || socialId.length() > 100) {
			return ResponseEntity.status(401)
					.body(Map.of("code", 9402, "message", "required_authorization"));
		}

		return ResponseEntity.status(201).body(Map.of("code", 9204, "message", "register_success"));
	}
}
