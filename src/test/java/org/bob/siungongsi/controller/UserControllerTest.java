package org.bob.siungongsi.controller;

import static org.bob.siungongsi.dto.ApiResponseCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.bob.siungongsi.config.TestSecurityConfig;
import org.bob.siungongsi.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.controller.dto.UserResponse.NotificationStatusResponse;
import org.bob.siungongsi.controller.dto.UserSubscriptionsResponse;
import org.bob.siungongsi.controller.dto.UserSubscriptionsResponse.SubscribedCompany;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.exception.GlobalExceptionHandler;
import org.bob.siungongsi.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * UserController 테스트
 *
 * 이 테스트 클래스는 테스트 중에 Spring Security를 비활성화하기 위해 TestSecurityConfig를 import합니다. 이를 통해 테스트 기대값이
 * 컨트롤러 동작과 일치할 수 있습니다. 또한 인증된 사용자를 시뮬레이션하기 위해 모의(SecurityContext)를 설정합니다.
 */
@ActiveProfiles({"test"})
@WebMvcTest(controllers = {UserController.class, GlobalExceptionHandler.class})
@Import(TestSecurityConfig.class)
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService userService;

  @BeforeEach
  public void setup() {
    // Setup security context with a mock authentication for user ID 1
    Authentication auth = new TestingAuthenticationToken(1L, null, "ROLE_USER");
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(auth);
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  /** 알림 허용 여부 조회 테스트 */
  @Test
  @DisplayName("TC001: 정상적인 알림 허용 여부 조회")
  public void testGetNotificationStatusSuccess() throws Exception {
    // given
    NotificationStatusResponse response = new NotificationStatusResponse(1L, true);
    when(userService.getNotificationStatus()).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/users/notification-status").header("Authorization", "Bearer VALIDTOKEN"))
        .andDo(print()) // Print request/response for debugging
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(USER_GET_STATUS.getCode()))
        .andExpect(jsonPath("$.message").value(USER_GET_STATUS.getMessage()))
        .andExpect(jsonPath("$.data.userId").value(1))
        .andExpect(jsonPath("$.data.notificationFlag").value(true));
  }

  @Test
  @DisplayName("TC002: 인증되지 않은 사용자 접근")
  public void testGetNotificationStatusUnauthorized() throws Exception {
    // Clear the security context to simulate no authentication
    SecurityContextHolder.clearContext();

    // given
    when(userService.getNotificationStatus())
        .thenThrow(new CustomException(USER_REQUIRED_AUTHORIZATION, "Authorization is required"));

    // when & then
    mockMvc
        .perform(get("/v1/users/notification-status"))
        .andDo(print()) // Print request/response for debugging
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(USER_REQUIRED_AUTHORIZATION.getCode()))
        .andExpect(jsonPath("$.message").value(USER_REQUIRED_AUTHORIZATION.getMessage()));
  }

  @Test
  @DisplayName("TC003: accessToken이 누락됨")
  public void testGetNotificationStatusMissingToken() throws Exception {
    // Clear the security context to simulate no authentication
    SecurityContextHolder.clearContext();

    // given
    when(userService.getNotificationStatus())
        .thenThrow(new CustomException(USER_REQUIRED_AUTHORIZATION, "Authorization is required"));

    // when & then
    mockMvc
        .perform(get("/v1/users/notification-status").header("Authorization", ""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(USER_REQUIRED_AUTHORIZATION.getCode()))
        .andExpect(jsonPath("$.message").value(USER_REQUIRED_AUTHORIZATION.getMessage()));
  }

  @Test
  @DisplayName("TC004: accessToken이 공백")
  public void testGetNotificationStatusEmptyToken() throws Exception {
    // Clear the security context to simulate no authentication
    SecurityContextHolder.clearContext();

    // given
    when(userService.getNotificationStatus())
        .thenThrow(new CustomException(USER_REQUIRED_AUTHORIZATION, "Authorization is required"));

    // when & then
    mockMvc
        .perform(
            get("/v1/users/notification-status")
                .header("Authorization", "")) // Empty string for header value
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(USER_REQUIRED_AUTHORIZATION.getCode()))
        .andExpect(jsonPath("$.message").value(USER_REQUIRED_AUTHORIZATION.getMessage()));
  }

  /** 알림 허용 상태 변경 테스트 */
  @Test
  @DisplayName("TC005: 알림 허용 상태를 변경 (ON → OFF)")
  public void testUpdateNotificationStatusOnToOff() throws Exception {
    // given
    UserNotificationRequest request = new UserNotificationRequest(false, "device-token-123");
    NotificationStatusResponse response = new NotificationStatusResponse(1L, false);
    when(userService.updateNotificationStatus(any(UserNotificationRequest.class)))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(
            patch("/v1/users/notification-status")
                .header("Authorization", "Bearer VALIDTOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value(USER_UPDATE_STATUS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.message").value(USER_UPDATE_STATUS_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.data.userId").value(1))
        .andExpect(jsonPath("$.data.notificationFlag").value(false));
  }

  @Test
  @DisplayName("TC006: 알림 허용 상태를 변경 (OFF → ON)")
  public void testUpdateNotificationStatusOffToOn() throws Exception {
    // given
    UserNotificationRequest request = new UserNotificationRequest(true, "device-token-123");
    NotificationStatusResponse response = new NotificationStatusResponse(1L, true);
    when(userService.updateNotificationStatus(any(UserNotificationRequest.class)))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(
            patch("/v1/users/notification-status")
                .header("Authorization", "Bearer VALIDTOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value(USER_UPDATE_STATUS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.message").value(USER_UPDATE_STATUS_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.data.userId").value(1))
        .andExpect(jsonPath("$.data.notificationFlag").value(true));
  }

  @Test
  @DisplayName("TC007: 동일한 상태로 변경 요청")
  public void testUpdateNotificationStatusSameValue() throws Exception {
    // given
    UserNotificationRequest request = new UserNotificationRequest(true, "device-token-123");
    when(userService.updateNotificationStatus(any(UserNotificationRequest.class)))
        .thenThrow(
            new CustomException(USER_STATUS_ALREADY_EXIST, "Notification status already exists"));

    // when & then
    mockMvc
        .perform(
            patch("/v1/users/notification-status")
                .header("Authorization", "Bearer VALIDTOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(USER_STATUS_ALREADY_EXIST.getCode()))
        .andExpect(jsonPath("$.message").value(USER_STATUS_ALREADY_EXIST.getMessage()));
  }

  @Test
  @DisplayName("TC008: 인증되지 않은 사용자 접근")
  public void testUpdateNotificationStatusUnauthorized() throws Exception {
    // Clear the security context to simulate no authentication
    SecurityContextHolder.clearContext();

    // given
    UserNotificationRequest request = new UserNotificationRequest(true, "device-token-123");
    when(userService.updateNotificationStatus(any(UserNotificationRequest.class)))
        .thenThrow(new CustomException(USER_REQUIRED_AUTHORIZATION, "Authorization is required"));

    // when & then
    mockMvc
        .perform(
            patch("/v1/users/notification-status")
                .header("Authorization", "Bearer INVALIDTOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(USER_REQUIRED_AUTHORIZATION.getCode()))
        .andExpect(jsonPath("$.message").value(USER_REQUIRED_AUTHORIZATION.getMessage()));
  }

  @Test
  @DisplayName("TC009: 잘못된 데이터 형식 입력")
  public void testUpdateNotificationStatusInvalidData() throws Exception {
    // given
    String invalidRequest =
        "{\"notificationFlag\":\"INVALID DATA\", \"pushToken\":\"device-token-123\"}";

    // Add custom expectation for invalid JSON
    when(userService.updateNotificationStatus(any(UserNotificationRequest.class)))
        .thenThrow(new CustomException(USER_INVALID_DATA_FORMAT, "Invalid data format"));

    // when & then
    mockMvc
        .perform(
            patch("/v1/users/notification-status")
                .header("Authorization", "Bearer VALIDTOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
        .andExpect(status().isBadRequest());
  }

  /** 회원 알림 설정 기업 목록 조회 테스트 */
  @Test
  @DisplayName("TC010: 정상적인 사용자 구독 정보 조회")
  public void testGetUserSubscriptionsSuccess() throws Exception {
    // given
    List<SubscribedCompany> subscribedCompanies =
        Arrays.asList(
            new SubscribedCompany(1L, "삼성전자", "005930", "KR7005930003"),
            new SubscribedCompany(2L, "현대자동차", "005380", "KR7005380001"));
    UserSubscriptionsResponse response = new UserSubscriptionsResponse(1L, subscribedCompanies);
    when(userService.getUserSubscriptions()).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/users/subscriptions").header("Authorization", "Bearer VALIDTOKEN"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(USER_SUBSCRIPTIONS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.message").value(USER_SUBSCRIPTIONS_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.data.userId").value(1))
        .andExpect(jsonPath("$.data.subscribedCompanies[0].companyId").value(1))
        .andExpect(jsonPath("$.data.subscribedCompanies[0].companyName").value("삼성전자"))
        .andExpect(jsonPath("$.data.subscribedCompanies[0].companyCode").value("005930"))
        .andExpect(jsonPath("$.data.subscribedCompanies[1].companyId").value(2))
        .andExpect(jsonPath("$.data.subscribedCompanies[1].companyName").value("현대자동차"))
        .andExpect(jsonPath("$.data.subscribedCompanies[1].companyCode").value("005380"));
  }

  @Test
  @DisplayName("TC011: 구독 정보가 없는 사용자 조회")
  public void testGetUserSubscriptionsEmpty() throws Exception {
    // given
    UserSubscriptionsResponse response = new UserSubscriptionsResponse(1L, List.of());
    when(userService.getUserSubscriptions()).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/users/subscriptions").header("Authorization", "Bearer VALIDTOKEN"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(USER_SUBSCRIPTIONS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.message").value(USER_SUBSCRIPTIONS_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.data.userId").value(1))
        .andExpect(jsonPath("$.data.subscribedCompanies").isArray())
        .andExpect(jsonPath("$.data.subscribedCompanies").isEmpty());
  }

  @Test
  @DisplayName("TC012: 인증되지 않은 사용자 접근")
  public void testGetUserSubscriptionsUnauthorized() throws Exception {
    // Clear the security context to simulate no authentication
    SecurityContextHolder.clearContext();

    // given
    when(userService.getUserSubscriptions())
        .thenThrow(new CustomException(USER_REQUIRED_AUTHORIZATION, "Authorization is required"));

    // when & then
    mockMvc
        .perform(get("/v1/users/subscriptions").header("Authorization", "Bearer INVALIDTOKEN"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(USER_REQUIRED_AUTHORIZATION.getCode()))
        .andExpect(jsonPath("$.message").value(USER_REQUIRED_AUTHORIZATION.getMessage()));
  }

  @Test
  @DisplayName("TC013: 토큰이 누락된 요청")
  public void testGetUserSubscriptionsMissingToken() throws Exception {
    // Clear the security context to simulate no authentication
    SecurityContextHolder.clearContext();

    // given
    when(userService.getUserSubscriptions())
        .thenThrow(new CustomException(USER_REQUIRED_AUTHORIZATION, "Authorization is required"));

    // when & then
    mockMvc
        .perform(get("/v1/users/subscriptions"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(USER_REQUIRED_AUTHORIZATION.getCode()))
        .andExpect(jsonPath("$.message").value(USER_REQUIRED_AUTHORIZATION.getMessage()));
  }

  @Test
  @DisplayName("TC014: 서버 내부 오류 발생")
  public void testGetUserSubscriptionsServerError() throws Exception {
    // given
    when(userService.getUserSubscriptions())
        .thenThrow(new CustomException(USER_INTERNAL_SERVER_ERROR, "Internal server error"));

    // when & then
    mockMvc
        .perform(get("/v1/users/subscriptions").header("Authorization", "Bearer VALIDTOKEN"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(USER_INTERNAL_SERVER_ERROR.getCode()))
        .andExpect(jsonPath("$.message").value(USER_INTERNAL_SERVER_ERROR.getMessage()));
  }
}
