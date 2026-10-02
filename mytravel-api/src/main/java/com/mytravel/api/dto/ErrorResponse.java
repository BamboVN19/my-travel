package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private Instant timestamp;
  private int status;
  private String error;
  private String message;
  private List<FieldErrorDetail> errors;
  private String path;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FieldErrorDetail {
    private String field;
    private String message;
  }
}
