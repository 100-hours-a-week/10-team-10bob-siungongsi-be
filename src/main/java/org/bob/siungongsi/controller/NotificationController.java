package org.bob.siungongsi.controller;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notification API", description = "알림 관련 API")
@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

  @Operation(summary = "추천 알림 기업", description = "이 API는 구독자수가 많은 상위 5개의 기업을 사용자에게 추천해주는 API입니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "추천 기업 목록 조회 성공",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - Authorization 헤더가 없거나 잘못된 경우",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
      })
  @GetMapping("/recommended-companies")
  public void getRecommendedCompanies(@RequestHeader("Authorization") String authorization) {}

  @Operation(summary = "알림 추가", description = "해당 API는 사용자가 특정 회사의 공시 알림을 받을 수 있도록 설정합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "알림 설정 성공",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "잘못된 companyId", content = @Content),
        @ApiResponse(responseCode = "400", description = "이미 알림이 존재함", content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - Authorization 헤더 필요",
            content = @Content),
        @ApiResponse(responseCode = "409", description = "알림 상태 확인 필요", content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
      })
  @PostMapping
  public void addNotification(
      @RequestHeader("Authorization") String authorization,
      @RequestBody NotificationRequest request) {}

  public class NotificationRequest {
    private int companyId;

    // Getter, Setter
    public int getCompanyId() {
      return companyId;
    }

    public void setCompanyId(int companyId) {
      this.companyId = companyId;
    }
  }

  @Operation(summary = "알림 제거", description = "특정 기업의 알림을 해제하는 API입니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "알림 해제 성공",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "잘못된 companyId", content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - Authorization 헤더 필요",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "알림이 존재하지 않음", content = @Content),
        @ApiResponse(responseCode = "409", description = "알림 상태 확인 필요", content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
      })
  @DeleteMapping("/{companyId}")
  public void removeNotification(
      @RequestHeader("Authorization") String authorization,
      @Parameter(description = "알림을 해제할 회사 ID", required = true) @PathVariable("companyId")
          int companyId) {}
}
