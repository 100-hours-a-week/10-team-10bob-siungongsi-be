package org.bob.siungongsi.controller;

import org.bob.siungongsi.controller.dto.NotificationRequest.NotificationCompanyRequest;
import org.bob.siungongsi.controller.spec.NotificationControllerSpec;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController implements NotificationControllerSpec {

  @GetMapping("/recommended-companies")
  public ResponseEntity<ApiResponseWrapper<?>> getRecommendedCompanies(
      @RequestHeader("Authorization") String authorization) {
    return null;
  }

  @PostMapping
  public ResponseEntity<ApiResponseWrapper<?>> addNotification(
      @RequestHeader("Authorization") String authorization,
      @RequestBody NotificationCompanyRequest request) {
    return null;
  }

  @DeleteMapping("/{companyId}")
  public ResponseEntity<ApiResponseWrapper<?>> removeNotification(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("companyId") int companyId) {
    return null;
  }
}
