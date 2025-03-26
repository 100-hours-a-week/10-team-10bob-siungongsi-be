package org.bob.siungongsi.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.bob.siungongsi.controller.dto.CompanyResponse;
import org.bob.siungongsi.controller.dto.GongsiResponse;
import org.bob.siungongsi.controller.dto.GongsiResponse.GongsiItem;
import org.bob.siungongsi.controller.dto.GongsiResponse.GongsiListResponse;
import org.bob.siungongsi.controller.dto.PaginationResponse;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.NotificationRepository;
import org.bob.siungongsi.service.GongsiService;
import org.bob.siungongsi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(GongsiController.class)
class GongsiControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper om;

  @MockBean private GongsiService gongsiService;

  @MockBean private UserService userService;

  @MockBean private NotificationRepository notificationRepository;

  private static final String REQUEST_URL = "/v1/gongsi";

  private GongsiListResponse createMockGongsiListResponse(int size, int page, int totalPages) {
    List<GongsiItem> gongsiList = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      gongsiList.add(
          GongsiItem.of(
              (long) (i + 1),
              "공시 제목 " + (i + 1),
              "테스트 회사",
              "2024-03-01 12:00:00",
              100 - i,
              page > 1 ? null : "공시 내용 " + (i + 1)));
    }
    long totalResults = size * totalPages;
    PaginationResponse pagination = PaginationResponse.of(page, totalPages, totalResults);
    return GongsiListResponse.of(gongsiList, gongsiList.size(), pagination);
  }

  @Test
  @DisplayName("기본 조회 시 공시 목록을 반환한다 (TC001)")
  void givenDefaultParameters_whenGetGongsiList_thenReturnsList() throws Exception {
    // Given
    GongsiListResponse response = createMockGongsiListResponse(8, 1, 10);
    when(gongsiService.getGongsiList(null, "latest", false, 1, 8, null, null)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiListSize").value(8))
        .andExpect(jsonPath("$.data.pagination.currentPage").value(1));
  }

  @Test
  @DisplayName("특정 회사의 공시 목록을 조회한다 (TC002)")
  void givenCompanyId_whenGetGongsiList_thenReturnsCompanyGongsiList() throws Exception {
    // Given
    Long companyId = 1L;
    GongsiListResponse response = createMockGongsiListResponse(5, 1, 3);
    when(gongsiService.getGongsiList(companyId, "latest", false, 1, 8, null, null))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("companyId", companyId.toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiListSize").value(5));
  }

  @Test
  @DisplayName("정렬 방식을 최신순으로 지정하여 조회한다 (TC003)")
  void givenLatestSort_whenGetGongsiList_thenReturnsLatestGongsiList() throws Exception {
    // Given
    String sort = "latest";
    GongsiListResponse response = createMockGongsiListResponse(8, 1, 10);
    when(gongsiService.getGongsiList(null, sort, false, 1, 8, null, null)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("sort", sort)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid", " "})
  @DisplayName("잘못된 정렬 방식 입력 시 invalid_sort_type 오류를 반환한다 (TC008, TC026)")
  void givenInvalidSortTypes_whenGetGongsiList_thenReturnsError(String sort) throws Exception {
    // Given
    when(gongsiService.getGongsiList(null, sort, false, 1, 8, null, null))
        .thenThrow(new IllegalArgumentException("Invalid sort type"));

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("sort", sort)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_INVALID_SORT_TYPE.getCode()))
        .andExpect(
            jsonPath("$.message").value(ApiResponseCode.GONGSI_INVALID_SORT_TYPE.getMessage()));
  }

  @Test
  @DisplayName("정렬 방식을 조회순으로 지정하여 조회한다 (TC004)")
  void givenViewsSort_whenGetGongsiList_thenReturnsViewsGongsiList() throws Exception {
    // Given
    String sort = "views";
    GongsiListResponse response = createMockGongsiListResponse(8, 1, 10);
    when(gongsiService.getGongsiList(null, sort, false, 1, 8, null, null)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("sort", sort)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("특정 날짜 범위의 공시를 조회한다 (TC005)")
  void givenDateRange_whenGetGongsiList_thenReturnsDateRangeGongsiList() throws Exception {
    // Given
    String startDate = "2024-03-01";
    String endDate = "2024-03-10";
    GongsiListResponse response = createMockGongsiListResponse(3, 1, 1);
    when(gongsiService.getGongsiList(null, "latest", false, 1, 8, startDate, endDate))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiListSize").value(3));
  }

  @Test
  @DisplayName("페이지네이션을 적용하여 조회한다 (TC006)")
  void givenPagination_whenGetGongsiList_thenReturnsPaginatedGongsiList() throws Exception {
    // Given
    int page = 2;
    int size = 5;
    GongsiListResponse response = createMockGongsiListResponse(size, page, 5);
    when(gongsiService.getGongsiList(null, "latest", false, page, size, null, null))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.pagination.currentPage").value(page))
        .andExpect(jsonPath("$.data.pagination.totalPages").value(5));
  }

  @Test
  @DisplayName("존재하지 않는 회사 ID 입력 시 company_not_found 오류를 반환한다 (TC007)")
  void givenNonExistentCompanyId_whenGetGongsiList_thenReturnsError() throws Exception {
    // Given
    Long companyId = 99999L;
    when(gongsiService.getGongsiList(companyId, "latest", false, 1, 8, null, null))
        .thenThrow(new IllegalArgumentException("Company not found"));

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("companyId", companyId.toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_COMPANY_NOT_FOUND.getCode()))
        .andExpect(
            jsonPath("$.message").value(ApiResponseCode.GONGSI_COMPANY_NOT_FOUND.getMessage()));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "2025-03-10,2025-03-01", // 잘못된 날짜 순서
        "20250301,20250310", // 잘못된 포맷
        ",2025-03-10", // startDate가 빈 문자열
        "2025-03-01,", // endDate가 빈 문자열
        ",", // 둘 다 빈 문자열
        "" // 빈 문자열 전체
      })
  @DisplayName("잘못된 날짜 입력 시 invalid_date_pair 오류를 반환한다 (TC009, TC010, TC022, TC023, TC027)")
  void givenInvalidDateRanges_whenGetGongsiList_thenReturnsError(String datePair) throws Exception {
    // 빈 문자열은 그대로 사용 (null로 변환하지 않음)
    String[] parts = datePair.split(",", -1);
    String startDate = parts.length > 0 ? parts[0].trim() : "";
    String endDate = parts.length > 1 ? parts[1].trim() : "";

    // 스텁에서 실제 전달될 파라미터(빈 문자열 포함)를 그대로 사용
    when(gongsiService.getGongsiList(null, "latest", false, 1, 8, startDate, endDate))
        .thenThrow(new IllegalArgumentException("Invalid date pair"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_INVALID_DATE_PAIR.getCode()))
        .andExpect(
            jsonPath("$.message").value(ApiResponseCode.GONGSI_INVALID_DATE_PAIR.getMessage()));
  }

  @Test
  @DisplayName("페이지 크기가 범위를 초과한 경우 오류를 반환한다 (TC011)")
  void givenExcessiveSizeParameter_whenGetGongsiList_thenReturnsError() throws Exception {
    // Given
    int size = 200;
    when(gongsiService.getGongsiList(null, "latest", false, 1, size, null, null))
        .thenThrow(new IllegalArgumentException("Page size exceeds maximum allowed value"));

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_INVALID_PAGE_SIZE.getCode()))
        .andExpect(
            jsonPath("$.message").value(ApiResponseCode.GONGSI_INVALID_PAGE_SIZE.getMessage()));
  }

  @Test
  @DisplayName("페이지 번호가 음수인 경우 오류를 반환한다 (TC012)")
  void givenNegativePageParameter_whenGetGongsiList_thenReturnsError() throws Exception {
    // Given
    int page = -1;
    when(gongsiService.getGongsiList(null, "latest", false, page, 8, null, null))
        .thenThrow(new IllegalArgumentException("Page number cannot be negative"));

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_INVALID_PAGE_NUMBER.getCode()))
        .andExpect(
            jsonPath("$.message").value(ApiResponseCode.GONGSI_INVALID_PAGE_NUMBER.getMessage()));
  }

  @Test
  @DisplayName("companyId가 null인 경우 전체 공시 목록을 반환한다 (TC013)")
  void givenNullCompanyId_whenGetGongsiList_thenReturnsAllGongsi() throws Exception {
    // Given
    GongsiListResponse response = createMockGongsiListResponse(8, 1, 10);
    when(gongsiService.getGongsiList(null, "latest", false, 1, 8, null, null)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiListSize").value(8));
  }

  @Test
  @DisplayName("sort가 null인 경우 기본 정렬(latest)을 적용한다 (TC014)")
  void givenNullSort_whenGetGongsiList_thenReturnsLatestSorted() throws Exception {
    // Given
    GongsiListResponse response = createMockGongsiListResponse(8, 1, 10);
    when(gongsiService.getGongsiList(null, "latest", false, 1, 8, null, null)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("content 값이 true인 경우 공시 내용을 포함하여 반환한다 (TC015)")
  void givenContentTrue_whenGetGongsiList_thenReturnsWithContent() throws Exception {
    // Given
    GongsiListResponse response = createMockGongsiListResponse(8, 1, 10);
    when(gongsiService.getGongsiList(null, "latest", true, 1, 8, null, null)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("content", "true")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("content 값이 null인 경우 기본값 false를 적용한다 (TC016)")
  void givenNullContent_whenGetGongsiList_thenReturnsWithoutContent() throws Exception {
    // Given
    GongsiListResponse response = createMockGongsiListResponse(8, 1, 10);
    when(gongsiService.getGongsiList(null, "latest", false, 1, 8, null, null)).thenReturn(response);

    // When & Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("page=1 (최소 페이지)인 경우 첫 번째 페이지를 반환한다 (TC017)")
  void givenMinimumPage_whenGetGongsiList_thenReturnsFirstPage() throws Exception {
    // Given
    int page = 1;
    GongsiListResponse response = createMockGongsiListResponse(8, page, 10);
    when(gongsiService.getGongsiList(null, "latest", false, page, 8, null, null))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.pagination.currentPage").value(page));
  }

  @Test
  @DisplayName("page=1000 (최대 페이지)인 경우 마지막 페이지를 반환한다 (TC018)")
  void givenMaximumPage_whenGetGongsiList_thenReturnsLastPage() throws Exception {
    // Given
    int page = 1000;
    GongsiListResponse response = createMockGongsiListResponse(0, page, 1000);
    when(gongsiService.getGongsiList(null, "latest", false, page, 8, null, null))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.pagination.currentPage").value(page));
  }

  @Test
  @DisplayName("size=1 (최소 크기)인 경우 1개 데이터를 반환한다 (TC019)")
  void givenMinimumSize_whenGetGongsiList_thenReturnsOneItem() throws Exception {
    // Given
    int size = 1;
    GongsiListResponse response = createMockGongsiListResponse(size, 1, 100);
    when(gongsiService.getGongsiList(null, "latest", false, 1, size, null, null))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiListSize").value(size));
  }

  @Test
  @DisplayName("size=10 (최대 크기)인 경우 10개 데이터를 반환한다 (TC020)")
  void givenMaximumSize_whenGetGongsiList_thenReturnsMaxItems() throws Exception {
    // Given
    int size = 10;
    GongsiListResponse response = createMockGongsiListResponse(size, 1, 10);
    when(gongsiService.getGongsiList(null, "latest", false, 1, size, null, null))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiListSize").value(size));
  }

  @Test
  @DisplayName("startDate와 endDate가 같은 경우 해당 날짜의 공시만 반환한다 (TC021)")
  void givenSameStartAndEndDate_whenGetGongsiList_thenReturnsSingleDayGongsi() throws Exception {
    // Given
    String startDate = "2025-03-10";
    String endDate = "2025-03-10";
    GongsiListResponse response = createMockGongsiListResponse(3, 1, 1);
    when(gongsiService.getGongsiList(null, "latest", false, 1, 8, startDate, endDate))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUEST_URL)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiListSize").value(3));
  }

  // --- Gongsi 상세 조회 테스트 ---

  private static final String DETAIL_REQUEST_URL = "/v1/gongsi/{gongsiId}";

  @Test
  @DisplayName("특정 공시 상세 조회 (TC028)")
  void givenValidGongsiId_whenGetGongsiDetail_thenReturnsGongsiDetail() throws Exception {
    // Given
    Long gongsiId = 101L;
    GongsiResponse.GongsiInfo gongsiInfo =
        GongsiResponse.GongsiInfo.of(
            gongsiId,
            "테스트 공시 제목",
            "24.03.01 12:00",
            100,
            "공시 상세 내용입니다.",
            "https://example.com/gongsi/101");
    CompanyResponse.CompanyInfo companyInfo =
        CompanyResponse.CompanyInfo.of(1, "테스트 회사", 25.5, false);
    GongsiResponse.GongsiDetailResponse detailResponse =
        GongsiResponse.GongsiDetailResponse.of(gongsiInfo, companyInfo);

    when(gongsiService.getGongsiDetail(eq(gongsiId), anyString())).thenReturn(detailResponse);

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(DETAIL_REQUEST_URL, gongsiId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_DETAIL_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsi.id").value(gongsiId))
        .andExpect(jsonPath("$.data.gongsi.title").value("테스트 공시 제목"))
        .andExpect(jsonPath("$.data.gongsi.content").value("공시 상세 내용입니다."))
        .andExpect(jsonPath("$.data.company.name").value("테스트 회사"))
        .andExpect(jsonPath("$.data.company.prdyCtr").value(25.5))
        .andExpect(jsonPath("$.data.company.isSubscribed").value(false));
  }

  @Test
  @DisplayName("존재하지 않는 공시 조회 (TC029)")
  void givenNonExistentGongsiId_whenGetGongsiDetail_thenReturnsNotFound() throws Exception {
    // Given
    Long gongsiId = 9999L;
    when(gongsiService.getGongsiDetail(eq(gongsiId), anyString()))
        .thenThrow(
            new CustomException(
                ApiResponseCode.GONGSI_NOT_FOUND, "Gongsi not found with ID: " + gongsiId));

    // When & Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(DETAIL_REQUEST_URL, gongsiId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_NOT_FOUND.getCode()))
        .andExpect(jsonPath("$.message").value(ApiResponseCode.GONGSI_NOT_FOUND.getMessage()));
  }

  @Test
  @DisplayName("잘못된 공시 ID 형식 입력 시 500 오류를 반환한다 (TC030)")
  void givenInvalidGongsiIdFormat_whenGetGongsiDetail_thenReturnsInternalServerError()
      throws Exception {
    // When: 잘못된 형식의 gongsiId("abc") 요청 시 500 Internal Server Error 반환
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/v1/gongsi/abc").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR.getCode()))
        .andExpect(
            jsonPath("$.message").value(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR.getMessage()));
  }

  @Test
  @DisplayName("gongsiId가 null인 경우 500 오류를 반환한다 (TC031)")
  void givenNullGongsiId_whenGetGongsiDetail_thenReturnsInternalServerError() throws Exception {
    // When: URL에 null 값을 전달할 수 없으므로 500 Internal Server Error 반환
    mockMvc
        .perform(MockMvcRequestBuilders.get("/v1/gongsi/").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR.getCode()))
        .andExpect(
            jsonPath("$.message").value(ApiResponseCode.GONGSI_INTERNAL_SERVER_ERROR.getMessage()));
  }
}
