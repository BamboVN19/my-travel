package com.travel.mytravel.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final boolean IS_MOCK = false; // Đổi thành true để test hoặc false kết nối Spring Boot thật
    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";

    private static Retrofit retrofit = null;
    private static TokenManager tokenManager = null;

    public static void init(Context context) {
        if (tokenManager == null && context != null) {
            tokenManager = new TokenManager(context.getApplicationContext());
        }
    }

    public static ApiService getService() {
        if (IS_MOCK) {
            return new MockApiService();
        }

        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();

                    String token = tokenManager != null ? tokenManager.getAccessToken() : null;
                    if (token != null && !token.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }

                    requestBuilder.header("Content-Type", "application/json");
                    Request request = requestBuilder.build();

                    try {
                        Log.d("MYTRAVEL_API", "➔ [REQUEST] " + request.method() + " " + request.url() 
                            + (token != null ? " [Authorization Header Present]" : " [No Token]"));
                    } catch (Throwable ignored) {}

                    Response response = chain.proceed(request);

                    try {
                        ResponseBody responseBody = response.body();
                        String bodyString = "";
                        if (responseBody != null) {
                            BufferedSource source = responseBody.source();
                            source.request(Long.MAX_VALUE);
                            Buffer buffer = source.getBuffer();
                            bodyString = buffer.clone().readString(StandardCharsets.UTF_8);
                        }

                        Log.d("MYTRAVEL_API", "⬅ [RESPONSE " + response.code() + "] " + request.url() + "\n[RESPONSE BODY]: " + bodyString);
                    } catch (Throwable ignored) {}

                    return response;
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }

    public static TokenManager getTokenManager() {
        return tokenManager;
    }

    
    /**
     * Trích xuất thông báo lỗi chi tiết từ backend response body & mã HTTP status code
     */
    public static String getErrorMessage(retrofit2.Response<?> response, String defaultMessage) {
        if (response == null) return defaultMessage;

        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                if (errorJson != null && !errorJson.trim().isEmpty()) {
                    JSONObject obj = new JSONObject(errorJson);

                    // Check field-level errors array
                    if (obj.has("errors") && !obj.isNull("errors")) {
                        JSONArray fieldErrors = obj.optJSONArray("errors");
                        if (fieldErrors != null && fieldErrors.length() > 0) {
                            JSONObject firstError = fieldErrors.optJSONObject(0);
                            if (firstError != null && firstError.has("message")) {
                                String msg = firstError.optString("message");
                                if (!msg.trim().isEmpty()) {
                                    return msg;
                                }
                            }
                        }
                    }

                    if (obj.has("message") && !obj.isNull("message")) {
                        String msg = obj.getString("message");
                        if (!msg.trim().isEmpty()) {
                            return msg;
                        }
                    }

                    if (obj.has("error") && !obj.isNull("error")) {
                        String err = obj.getString("error");
                        if (!err.trim().isEmpty()) {
                            return err;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("MYTRAVEL_API", "Error parsing error body: " + e.getMessage());
        }

        switch (response.code()) {
            case 400:
                return "Yêu cầu không hợp lệ (Mã 400)! Vui lòng kiểm tra dữ liệu.";
            case 401:
                return "Phiên đăng nhập đã hết hạn hoặc không hợp lệ (Mã 401)!";
            case 403:
                return "Bạn không có quyền thực hiện thao tác này (Mã 403)!";
            case 404:
                return "Dữ liệu yêu cầu không tìm thấy (Mã 404)!";
            case 409:
                return "Dữ liệu bị trùng lặp hoặc xung đột hệ thống (Mã 409)!";
            case 500:
                return "Lỗi máy chủ nội bộ (Mã 500). Vui lòng thử lại sau!";
            case 502:
            case 503:
            case 504:
                return "Máy chủ đang bảo trì hoặc không phản hồi (Mã " + response.code() + ")!";
            default:
                return defaultMessage + " (Mã " + response.code() + ")";
        }
    }

    /**
     * Hiển thị Toast thông báo lỗi phản hồi API cho ứng dụng
     */
    public static void showError(Context context, retrofit2.Response<?> response, String defaultMessage) {
        String message = getErrorMessage(response, defaultMessage);
        Log.e("MYTRAVEL_API", "❌ API Failed [" + (response != null ? response.code() : 0) + "]: " + message);
        if (context != null) {
            Toast.makeText(context, "⚠️ " + message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Hiển thị Toast xử lý lỗi kết nối / ngoại lệ mạng
     */
    public static void handleFailure(Context context, Throwable t, String defaultMessage) {
        String msg = defaultMessage;
        if (t != null && t.getMessage() != null && !t.getMessage().trim().isEmpty()) {
            String errStr = t.getMessage().toLowerCase();
            if (errStr.contains("timeout")) {
                msg = "Kết nối máy chủ quá thời gian (Timeout)! Vui lòng thử lại.";
            } else if (errStr.contains("failed to connect") || errStr.contains("connection refused")) {
                msg = "Không thể kết nối đến máy chủ! Vui lòng kiểm tra kết nối mạng hoặc server.";
            } else {
                msg = defaultMessage + ": " + t.getMessage();
            }
        }
        Log.e("MYTRAVEL_API", "❌ Connection Failure: " + msg, t);
        if (context != null) {
            Toast.makeText(context, "⚠️ " + msg, Toast.LENGTH_LONG).show();
        }
    }

    public static String getInitialsFromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "MT";
        }
        String[] words = name.trim().split("\\s+");
        if (words.length == 1) {
            String w = words[0];
            if (w.length() >= 2) {
                return w.substring(0, 2).toUpperCase();
            }
            return w.toUpperCase();
        } else {
            String firstWord = words[0];
            String lastWord = words[words.length - 1];
            String firstChar = firstWord.length() > 0 ? firstWord.substring(0, 1) : "";
            String lastChar = lastWord.length() > 0 ? lastWord.substring(0, 1) : "";
            return (firstChar + lastChar).toUpperCase();
        }
    }

    public static String formatAvatarUrl(Context context, String url) {
        if (url != null && !url.trim().isEmpty()) {
            String trimmed = url.trim();
            if (trimmed.contains("localhost:8080")) {
                trimmed = trimmed.replace("localhost:8080", "10.0.2.2:8080");
            }
            if (trimmed.contains("127.0.0.1:8080")) {
                trimmed = trimmed.replace("127.0.0.1:8080", "10.0.2.2:8080");
            }
            if (trimmed.contains("10.0.2.2:8080/uploads")) {
                trimmed = trimmed.replace("10.0.2.2:8080/uploads", "10.0.2.2:8080/api/v1/uploads");
            }
            if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("data:") || trimmed.startsWith("content://") || trimmed.startsWith("file://")) {
                return trimmed;
            }
            if (trimmed.startsWith("/")) {
                return "http://10.0.2.2:8080/api/v1" + trimmed;
            }
            return "http://10.0.2.2:8080/api/v1/" + trimmed;
        }

        return null;
    }

    public static String formatAvatarUrl(Context context, String url, String username) {
        return formatAvatarUrl(context, url);
    }
}
