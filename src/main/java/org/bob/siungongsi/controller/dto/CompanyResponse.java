package org.bob.siungongsi.controller.dto;

import java.util.List;

public class CompanyResponse {
  public record CompanyNameListResponse(
      int companyNameListSize, List<CompanyNameResponse> companyNameList) {
    public static CompanyNameListResponse of(
        int companyNameListSize, List<CompanyNameResponse> companyNameList) {
      return new CompanyNameListResponse(companyNameListSize, companyNameList);
    }
  }

  public record CompanyNameResponse(long companyId, String companyName) {
    public static CompanyNameResponse of(long companyId, String companyName) {
      return new CompanyNameResponse(companyId, companyName);
    }
  }
}
