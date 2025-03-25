package org.bob.siungongsi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bob.siungongsi.controller.dto.CompanyResponse.CompanyNameListResponse;
import org.bob.siungongsi.controller.dto.CompanyResponse.CompanyNameResponse;
import org.bob.siungongsi.service.CompanyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles({"test", "dev"})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper om;

  @MockitoBean private CompanyService companyService;

  private static final String REQUEST_URL = "/v1/companies/name";

  @Test
  @DisplayName("기업명 일부 검색 시 해당 리스트를 반환한다 (TC001)")
  void givenPartialCompanyName_whenGetCompanyNames_thenReturnsList() throws Exception {
    // Given
    String keyword = "삼성";
    List<CompanyNameResponse> companyNames =
        Arrays.asList(
            CompanyNameResponse.of(1, "삼성전자"),
            CompanyNameResponse.of(2, "삼성물산"),
            CompanyNameResponse.of(3, "삼성생명"));
    CompanyNameListResponse response =
        CompanyNameListResponse.of(companyNames.size(), companyNames);

    when(companyService.getCompanyNames(keyword)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(4200))
        .andExpect(jsonPath("$.data.companyNameListSize").value(companyNames.size()))
        .andExpect(jsonPath("$.data.companyNameList.length()").value(companyNames.size()));
  }

  @Test
  @DisplayName("기업명 정확히 입력 시 해당 기업만 반환한다 (TC002)")
  void givenExactCompanyName_whenGetCompanyNames_thenReturnsExactMatch() throws Exception {
    // Given
    String keyword = "삼성전자";
    List<CompanyNameResponse> companyNames =
        Collections.singletonList(CompanyNameResponse.of(1L, "삼성전자"));
    CompanyNameListResponse response =
        CompanyNameListResponse.of(companyNames.size(), companyNames);

    when(companyService.getCompanyNames(keyword)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(4200))
        .andExpect(jsonPath("$.data.companyNameListSize").value(1))
        .andExpect(jsonPath("$.data.companyNameList[0].companyName").value("삼성전자"));
  }

  @Test
  @DisplayName("존재하지 않는 기업명 검색 시 빈 리스트를 반환한다 (TC003)")
  void givenNonExistentCompanyName_whenGetCompanyNames_thenReturnsEmptyList() throws Exception {
    // Given
    String keyword = "없음이요";
    List<CompanyNameResponse> companyNames = Collections.emptyList();
    CompanyNameListResponse response = CompanyNameListResponse.of(0, companyNames);

    when(companyService.getCompanyNames(keyword)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(4200))
        .andExpect(jsonPath("$.data.companyNameListSize").value(0))
        .andExpect(jsonPath("$.data.companyNameList.length()").value(0));
  }

  @Test
  @DisplayName("키워드 길이가 1자인 경우 관련 기업 리스트를 반환한다 (TC004)")
  void givenOneCharKeyword_whenGetCompanyNames_thenReturnsList() throws Exception {
    // Given
    String keyword = "s";
    List<CompanyNameResponse> companyNames =
        Arrays.asList(
            CompanyNameResponse.of(1, "SK하이닉스"),
            CompanyNameResponse.of(2, "SK텔레콤"),
            CompanyNameResponse.of(3, "SM엔터테인먼트"));
    CompanyNameListResponse response =
        CompanyNameListResponse.of(companyNames.size(), companyNames);

    when(companyService.getCompanyNames(keyword)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(4200))
        .andExpect(jsonPath("$.data.companyNameListSize").value(companyNames.size()));
  }

  @Test
  @DisplayName("키워드 길이가 18자인 경우 관련 기업 리스트를 반환한다 (TC005)")
  void given18CharsKeyword_whenGetCompanyNames_thenReturnsList() throws Exception {
    // Given
    String keyword = "abcdefghijklmnopqr";
    List<CompanyNameResponse> companyNames =
        Collections.singletonList(CompanyNameResponse.of(1L, "abcdefghijklmnopqr주식회사"));
    CompanyNameListResponse response =
        CompanyNameListResponse.of(companyNames.size(), companyNames);

    when(companyService.getCompanyNames(keyword)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(4200));
  }

  @Test
  @DisplayName("키워드 길이가 19자 이상인 경우 400 에러를 반환한다 (TC006)")
  void given19CharsKeyword_whenGetCompanyNames_thenReturnsError() throws Exception {
    // Given
    String keyword = "abcdefghijklmnopqrs";

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.code").value(4400));
  }

  @Test
  @DisplayName("키워드 길이가 0자인 경우 400 에러를 반환한다 (TC007)")
  void givenEmptyKeyword_whenGetCompanyNames_thenReturnsError() throws Exception {
    // Given
    String keyword = "";

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.code").value(4400));
  }
}
