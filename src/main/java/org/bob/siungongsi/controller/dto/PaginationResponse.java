package org.bob.siungongsi.controller.dto;

public record PaginationResponse(int currentPage, int totalPages, int totalResults) {
  public static PaginationResponse of(int currentPage, int totalPages, int totalResults) {
    return new PaginationResponse(currentPage, totalPages, totalResults);
  }
}
