package org.bob.siungongsi.controller;

import static org.bob.siungongsi.dto.ApiResponseCode.COMPANY_GET_NAME_LIST_SUCCESS;

import java.util.List;

import org.bob.siungongsi.controller.dto.CompanyResponse.CompanyNameListResponse;
import org.bob.siungongsi.controller.spec.CompanyControllerSpec;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/companies")
public class CompanyController implements CompanyControllerSpec {

  @GetMapping("/name")
  public ResponseEntity<ApiResponseWrapper<CompanyNameListResponse>> getCompanyNames(
      @RequestParam String keyword) {
    // 구현 없음 (Swagger 문서화만 유지)
    return ResponseEntity.ok(
        ApiResponseWrapper.success(
            COMPANY_GET_NAME_LIST_SUCCESS, CompanyNameListResponse.of(0, List.of())));
  }
}
