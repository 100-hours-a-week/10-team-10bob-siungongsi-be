package org.bob.siungongsi.controller.dto;

public class UserResponse {

  public record NotificationStatusResponse(int userId, boolean notificationFlag) {
    public static NotificationStatusResponse of(int userId, boolean notificationFlag) {
      return new NotificationStatusResponse(userId, notificationFlag);
    }
  }
}
