package org.bob.siungongsi.controller.spec;

import org.bob.siungongsi.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API", description = "회원 알림 허용 여부 조회 및 변경 API")
public interface UserControllerSpec {

  @Operation(
      summary = "알림 허용 여부 조회",
      description = "회원의 알림 허용 여부를 반환하는 API (설정 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "알림 상태 조회 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 상태 조회 성공",
                          value =
                              "{ \"code\": 3200, \"message\": \"get_notification_status\", \"data\": { \"userId\": 1, \"notificationFlag\": true } }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "JWT 토큰이 필요함",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "JWT 토큰 필요",
                          value = "{ \"code\": 3400, \"message\": \"required_authorization\" }")
                    })),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "서버 오류",
                          value = "{ \"code\": 3500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<?>> getNotificationStatus(
      @Parameter(
              description = "JWT 토큰 (Bearer 포함)",
              required = true,
              example = "Bearer your_token_here")
          String authorization);

  @Operation(
      summary = "알림 허용 상태 변경",
      description = "회원의 알림 허용 여부를 변경하는 API (설정 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "알림 허용 상태 변경 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 허용 상태 변경 성공",
                          value =
                              "{ \"code\": 3201, \"message\": \"update_notification_status_success\", \"data\": { \"userId\": 1, \"notificationFlag\": true } }")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "이미 같은 상태로 설정되어 있음",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 상태 이미 존재",
                          value =
                              "{ \"code\": 3401, \"message\": \"notification_status_already_exist\" }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "JWT 토큰이 필요함",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "JWT 토큰 필요",
                          value = "{ \"code\": 3400, \"message\": \"required_authorization\" }")
                    })),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "서버 오류",
                          value = "{ \"code\": 3500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<?>> updateNotificationStatus(
      @Parameter(
              description = "JWT 토큰 (Bearer 포함)",
              required = true,
              example = "Bearer your_token_here")
          String authorization,
      UserNotificationRequest request);
}
