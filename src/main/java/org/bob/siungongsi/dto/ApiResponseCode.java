package org.bob.siungongsi.dto;

public enum ApiResponseCode {
  // 공시 관련 응답 코드 (1)
  GONGSI_LIST_SUCCESS(1200, "get_gongsi_list_success"),
  GONGSI_INVALID_SORT_TYPE(1400, "invalid_sort_type"),
  GONGSI_INVALID_COMPANY_ID(1401, "invalid_company_id"),
  GONGSI_INVALID_DATE_PAIR(1402, "invalid_date_pair"),
  GONGSI_NOT_FOUND(1403, "gongsi_not_found"),
  GONGSI_COMPANY_NOT_FOUND(1404, "company_not_found"),
  GONGSI_INTERNAL_SERVER_ERROR(1500, "internal_server_error"),

  // 인증 관련 응답 코드 (2)
  AUTH_WITHDRAW_USER_SUCCESS(2200, "withdraw_user_success"),
  AUTH_GET_TERMS_SUCCESS(2201, "get_terms_success"),
  AUTH_LOGIN_SUCCESS(2202, "login_success"),
  AUTH_REGISTER_SUCCESS(2203, "register_success"),
  AUTH_REQUIRED_AUTHORIZATION(2400, "required_authorization"),
  AUTH_ACCESS_TOKEN_EXPIRED(2401, "access_token_expired"),
  AUTH_USER_NOT_FOUND(2402, "user_not_found"),
  AUTH_TERMS_NOT_FOUND(2403, "terms_not_found"),
  AUTH_INTERNAL_SERVER_ERROR(2500, "internal_server_error"),

  // 알림 관련 응답 코드 (3)
  USER_GET_STATUS(3200, "get_notification_status"),
  USER_UPDATE_STATUS_SUCCESS(3201, "update_notification_status_success"),
  USER_REQUIRED_AUTHORIZATION(3400, "required_authorization"),
  USER_STATUS_ALREADY_EXIST(3401, "notification_status_already_exist"),
  USER_INTERNAL_SERVER_ERROR(3500, "internal_server_error"),

  // 회사 관련 응답 코드 (4)
  COMPANY_GET_NAME_LIST_SUCCESS(4200, "get_company_name_list_success"),
  COMPANY_INVALID_KEYWORD_LENGTH(4400, "invalid_keyword_length"),
  COMPANY_INTERNAL_SERVER_ERROR(4500, "internal_server_error"),

  // 알림 관련 응답 코드 (5)
  NOTIFICATION_RECOMMENDED_COMPANY_SUCCESS(5200, "get_recomended_company_success"),
  NOTIFICATION_SUBSCRIPTION_SUCCESS(5201, "notification_subscription_success"),
  NOTIFICATION_UNSUBSCRIBE_SUCCESS(5202, "unsubscribe_notification_success"),
  NOTIFICATION_REQUIRED_AUTHORIZATION(5400, "required_authorization"),
  NOTIFICATION_INVALID_COMPANY_ID(5401, "invalid_company_id"),
  NOTIFICATION_ALREADY_EXISTS(5402, "notifications_already_exist"),
  NOTIFICATION_REQUIRED_STATUS(5403, "required_notification_status"),
  NOTIFICATION_NOT_FOUND(5404, "notification_not_found"),
  NOTIFICATION_INTERNAL_SERVER_ERROR(5500, "internal_server_error");

  private final int code;
  private final String message;

  ApiResponseCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
