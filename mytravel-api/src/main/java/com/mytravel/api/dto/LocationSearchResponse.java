package com.mytravel.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSearchResponse {
  private String placeId;
  private String name;
  private String formattedAddress;
  private Double latitude;
  private Double longitude;
  private Float rating;
  private List<String> types;
}
