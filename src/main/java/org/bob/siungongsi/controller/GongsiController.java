package org.bob.siungongsi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/gongsi") // 공시 API의 기본 경로
@Tag(name = "공시 API", description = "공시 정보를 조회하는 API") // Swagger에서 API 그룹을 지정
public class GongsiController {

	@GetMapping
	@Operation(summary = "공시 목록 조회", description = "공시 목록을 조회하는 API입니다. (메인 페이지, 공시 검색 페이지에서 사용)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "공시 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 유효하지 않은 정렬 방식)"),
		@ApiResponse(responseCode = "404", description = "공시 데이터를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public Map<String, Object> getGongsiList(
			@Parameter(description = "조회 대상 회사 ID", example = "1") @RequestParam(required = false)
					Integer companyId,
			@Parameter(description = "정렬 기준 (latest, views, oldest)", example = "latest")
					@RequestParam(defaultValue = "latest")
					String sort,
			@Parameter(description = "내용 포함 여부", example = "false")
					@RequestParam(defaultValue = "false")
					Boolean content,
			@Parameter(description = "페이지 번호 (1~100)", example = "1")
					@RequestParam(defaultValue = "1")
					Integer page,
			@Parameter(description = "페이지 크기 (10~100)", example = "8")
					@RequestParam(defaultValue = "8")
					Integer size,
			@Parameter(description = "시작 날짜 (yyyy-MM-dd)", example = "2025-03-01")
					@RequestParam(required = false)
					String startDate,
			@Parameter(description = "종료 날짜 (yyyy-MM-dd)", example = "2025-03-10")
					@RequestParam(required = false)
					String endDate) {
		return Map.of(
				"code",
				1200,
				"message",
				"get_gongsi_list_success",
				"data",
				Map.of(
						"gongsiList",
								List.of(
										Map.of(
												"gongsiId",
												1,
												"gongsiTitle",
												"삼성 모든 계열사 어쩌구...",
												"companyName",
												"삼성전자",
												"publishedDatetime",
												"25.03.12 16:04",
												"content",
												"느아아악 나는 배고프다"),
										Map.of(
												"gongsiId",
												2,
												"gongsiTitle",
												"삼성 모든 계열사 어쩌구...1",
												"companyName",
												"삼성전자",
												"publishedDatetime",
												"25.03.12 18:04",
												"content",
												"느아아악 나는 배고프다")),
						"gongsiListSize", 2,
						"pagination",
								Map.of("currentPage", 1, "totalPages", 3, "totalResults", 25)));
	}

	@GetMapping("/{gongsild}")
	@Operation(summary = "공시 상세 조회", description = "특정 공시의 상세 정보를 조회하는 API입니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "공시 상세 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 gongsild)"),
		@ApiResponse(responseCode = "404", description = "공시 데이터를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public Map<String, Object> getGongsiDetail(
			@Parameter(description = "공시 ID", example = "101", required = true)
					@PathVariable("gongsild")
					Integer gongsild) {

		return Map.of(
				"code",
				1201,
				"message",
				"get_detail_gongsi_success",
				"data",
				Map.of(
						"gongsi",
								Map.of(
										"id", gongsild,
										"title", "삼성전자, 새로운 반도체 기술 발표",
										"date", "25.02.25 16:04",
										"viewCount", 1200,
										"content", "삼성전자가 새로운 반도체 기술을 공개하며...",
										"originalUrl", "https://hellothere.xxx"),
						"company",
								Map.of(
										"id",
										1,
										"name",
										"삼성전자",
										"prdyCtr",
										2.43,
										"isSubscribed",
										false)));
	}
}
