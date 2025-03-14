package org.bob.siungongsi.controller.dto;

public class UserRequest {
  public record UserNotificationRequest(Boolean notificationFlag) {
    public static UserNotificationRequest of(Boolean notificationFlag) {
      return new UserNotificationRequest(notificationFlag);
    }
  }
}
