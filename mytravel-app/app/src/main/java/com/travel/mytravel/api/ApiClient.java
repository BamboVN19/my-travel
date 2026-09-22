package com.travel.mytravel.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
  private static final boolean IS_MOCK = true; // Đổi thành false khi muốn nối với Backend Java thật

  public static ApiService getService() {
    if (IS_MOCK) {
      return new MockApiService();
    }

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl("http://10.0.2.2:8080/")
      .addConverterFactory(GsonConverterFactory.create())
      .build();
    return retrofit.create(ApiService.class);
  }
}