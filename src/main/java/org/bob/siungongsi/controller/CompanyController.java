package org.bob.siungongsi.controller;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Company API", description = "기업 검색 관련 API")
@RestController
@RequestMapping("/v1/companies")
public class CompanyController {

  @Operation(summary = "기업명 검색", description = "키워드(자음, 모음 조합 단어)를 기반으로 기업명 자동완성 결과를 반환하는 API")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "기업명 자동완성 목록 조회 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompanyListResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 검색 키워드", content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
      })
  @GetMapping("/name")
  public void getCompanyNames(
      @Parameter(description = "검색 키워드 (1자 이상 18자 이하)", required = true) @RequestParam
          String keyword) {
    // 구현 없음 (Swagger 문서화만 유지)
  }

  // 기업 리스트 응답 포맷 (Swagger 문서화를 위한 Schema)
  static class CompanyListResponse {
    public int companyNameListSize;
    public Object companyNameList;
  }
}
