package org.bob.siungongsi.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/users") // 사용자 관련 API의 기본 경로
@Tag(name = "사용자 API", description = "회원 알림 허용 여부 조회 및 변경 API") // Swagger에서 API 그룹 지정
public class UserController {

	/** 알림 허용 여부 조회 API GET /v1/users/notification-status */
	@GetMapping("/notification-status")
	@Operation(summary = "알림 허용 여부 조회", description = "회원의 알림 허용 여부를 반환하는 API (설정 페이지에서 사용)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "알림 상태 조회 성공"),
		@ApiResponse(responseCode = "401", description = "JWT 토큰이 필요함"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<Map<String, Object>> getNotificationStatus(
			@Parameter(
							description = "JWT 토큰 (Bearer 포함)",
							required = true,
							example = "Bearer your_token_here")
					@RequestHeader("Authorization")
					String authorization) {

		return ResponseEntity.ok(
				Map.of(
						"code",
						3200,
						"message",
						"get_notification_status",
						"data",
						Map.of("userId", 1, "notificationFlag", true)));
	}

	/** 알림 허용 상태 변경 API PATCH /v1/users/notification-status */
	@PatchMapping("/notification-status")
	@Operation(summary = "알림 허용 상태 변경", description = "회원의 알림 허용 여부를 변경하는 API (설정 페이지에서 사용)")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "알림 허용 상태 변경 성공"),
		@ApiResponse(responseCode = "400", description = "이미 같은 상태로 설정되어 있음"),
		@ApiResponse(responseCode = "401", description = "JWT 토큰이 필요함"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<Map<String, Object>> updateNotificationStatus(
			@Parameter(
							description = "JWT 토큰 (Bearer 포함)",
							required = true,
							example = "Bearer your_token_here")
					@RequestHeader("Authorization")
					String authorization,
			@RequestBody Map<String, Boolean> requestBody) {

		Boolean notificationFlag = requestBody.get("notificationFlag");

		if (notificationFlag == null) {
			return ResponseEntity.status(400)
					.body(Map.of("code", 3401, "message", "notification_status_already_exist"));
		}

		return ResponseEntity.status(201)
				.body(
						Map.of(
								"code",
								3201,
								"message",
								"update_notification_status_success",
								"data",
								Map.of("userId", 1, "notificationFlag", notificationFlag)));
	}
}
