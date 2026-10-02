package com.mytravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportTourRequest {

  @NotNull(message = "Ngày bắt đầu chuyến đi không được để trống")
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate startDate;

  private String customTitle;
}
